package com.loserico.oauth2.mixin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.loserico.oauth2.deserializer.UsernamePasswordAuthenticationTokenDeserializer;

/**
 * This mixin class is used to serialize / deserialize
 * {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}. This class register
 * a custom deserializer {@link UsernamePasswordAuthenticationTokenDeserializer}.
 *
 * In order to use this mixin you'll need to add 3 more mixin classes.
 * <ol>
 *     <li>{@link UnmodifiableSetMixin}</li>
 *     <li>{@link SimpleGrantedAuthorityMixin}</li>
 *     <li>{@link UserMixin}</li>
 * </ol>
 *
 * <pre>
 *     ObjectMapper mapper = new ObjectMapper();
 *     mapper.registerModule(new CoreJackson2Module());
 * </pre>
 * @author Jitendra Singh
 * @since 4.2
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonDeserialize(using = UsernamePasswordAuthenticationTokenDeserializer.class)
public abstract class UsernamePasswordAuthenticationTokenMixin {
}
