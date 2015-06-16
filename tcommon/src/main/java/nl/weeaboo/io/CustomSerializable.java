package nl.weeaboo.io;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker interface to indicate that the annotated class uses custom serialization logic. This is used by
 * {@link CustomSerializableAnnotationProcessor} to emit a warning if the readObject/writeObject methods are
 * incorrectly implemented.
 */
@Retention(RetentionPolicy.CLASS)
public @interface CustomSerializable {

}
