package com.loserico.boot.mixin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.loserico.boot.deserializer.UserDeserializer;

/**
 * This mixin class helps in serialize/deserialize {@link org.springframework.security.core.userdetails.User}.
 * This class also register a custom deserializer {@link UserDeserializer} to deserialize User object successfully.
 * In order to use this mixin you need to register two more mixin classes in your ObjectMapper configuration.
 * <ol>
 *     <li>{@link SimpleGrantedAuthorityMixin}</li>
 *     <li>{@link UnmodifiableSetMixin}</li>
 * </ol>
 * <pre>
 *     ObjectMapper mapper = new ObjectMapper();
 *     mapper.registerModule(new CoreJackson2Module());
 * </pre>
 *
 * @author Jitendra Singh
 * @see UserDeserializer
 * @since 4.2
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
@JsonDeserialize(using = UserDeserializer.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class UserMixin {
}