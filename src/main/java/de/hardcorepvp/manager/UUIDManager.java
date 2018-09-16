package de.hardcorepvp.manager;

import de.hardcorepvp.Main;
import de.hardcorepvp.model.Callback;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class UUIDManager {

	public static final long FEBRUARY_2015 = 1422748800000L;

	private static Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

	private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
	private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";

	private static List<ProfileHolder> profileCache = new CopyOnWriteArrayList<>();

	private String name;
	private UUID id;

	public static Property getSkinData(Player player) {
		EntityPlayer playerNMS = ((CraftPlayer) player).getHandle();
		GameProfile profile = playerNMS.getProfile();
		Property property = profile.getProperties().get("textures").iterator().next();
		return new Property("textures", property.getValue(), property.getSignature());
	}

	public static void getSkinData(String name, Callback<String[]> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				URL getUUID = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
				InputStreamReader reader_0 = new InputStreamReader(getUUID.openStream());
				String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

				URL getSkin = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
				InputStreamReader reader_1 = new InputStreamReader(getSkin.openStream());
				JsonObject property = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
				String texture = property.get("value").getAsString();
				String signature = property.get("signature").getAsString();

				callback.onResult(new String[]{texture, signature});
			} catch (IOException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public static void getProfileHolderAt(String name, long timestamp, Callback<ProfileHolder> callback) {
		for (ProfileHolder profileHolder : profileCache) {
			if (profileHolder.getName().equals(name)) {
				callback.onResult(profileHolder);
				return;
			}
		}
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, timestamp / 1000)).openConnection();
			connection.setReadTimeout(5000);
			UUIDManager data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDManager.class);

			ProfileHolder profileHolder = new ProfileHolder(data.id, name);
			profileCache.add(profileHolder);

			callback.onResult(profileHolder);
		} catch (Exception e) {
			e.printStackTrace();
			callback.onFailure(e.getCause());
		}
	}

	public static void getProfileHolderAt(UUID uniqueId, Callback<ProfileHolder> callback) {
		for (ProfileHolder profileHolder : profileCache) {
			if (profileHolder.getUniqueId().equals(uniqueId)) {
				callback.onResult(profileHolder);
				return;
			}
		}
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uniqueId))).openConnection();
			connection.setReadTimeout(5000);
			UUIDManager[] nameHistory = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDManager[].class);
			UUIDManager currentNameData = nameHistory[nameHistory.length - 1];

			ProfileHolder profileHolder = new ProfileHolder(uniqueId, currentNameData.name.toLowerCase());
			profileCache.add(profileHolder);

			callback.onResult(profileHolder);
		} catch (Exception e) {
			e.printStackTrace();
			callback.onFailure(e.getCause());
		}
	}

	public static class ProfileHolder {

		private UUID uniqueId;
		private String name;

		public ProfileHolder(UUID uniqueId, String name) {
			this.uniqueId = uniqueId;
			this.name = name;
		}

		public UUID getUniqueId() {
			return uniqueId;
		}

		public String getName() {
			return name;
		}
	}
}
