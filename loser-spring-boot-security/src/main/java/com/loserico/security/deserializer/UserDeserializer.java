package com.loserico.security.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.loserico.security.mixin.UserMixin;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Set;

/**
 * Custom Deserializer for {@link User} class. This is already registered with {@link UserMixin}.
 * You can also use it directly with your mixin class.
 *
 * @author Jitendra Singh
 * @see UserMixin
 * @since 4.2
 */
public class UserDeserializer extends JsonDeserializer<User> {

	/**
	 * This method will create {@link User} object. It will ensure successful object creation even if password key is null in
	 * serialized json, because credentials may be removed from the {@link User} by invoking {@link User#eraseCredentials()}.
	 * In that case there won't be any password key in serialized json.
	 *
	 * @param jp the JsonParser
	 * @param ctxt the DeserializationContext
	 * @return the user
	 * @throws IOException if a exception during IO occurs
	 * @throws JsonProcessingException if an error during JSON processing occurs
	 */
	@Override
	public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		JsonNode jsonNode = mapper.readTree(jp);
		Set<SimpleGrantedAuthority> authorities = mapper.convertValue(jsonNode.get("authorities"), new TypeReference<Set<SimpleGrantedAuthority>>() {
		});
		JsonNode password = readJsonNode(jsonNode, "password");
		User result =  new User(
				readJsonNode(jsonNode, "username").asText(), password.asText(""),
				readJsonNode(jsonNode, "enabled").asBoolean(), readJsonNode(jsonNode, "accountNonExpired").asBoolean(),
				readJsonNode(jsonNode, "credentialsNonExpired").asBoolean(),
				readJsonNode(jsonNode, "accountNonLocked").asBoolean(), authorities
		);

		if (password.asText(null) == null) {
			result.eraseCredentials();
		}
		return result;
	}

	private JsonNode readJsonNode(JsonNode jsonNode, String field) {
		return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
	}
}
