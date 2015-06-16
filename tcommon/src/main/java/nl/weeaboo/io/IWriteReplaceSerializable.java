package nl.weeaboo.io;

import java.io.ObjectStreamException;
import java.io.Serializable;

public interface IWriteReplaceSerializable extends Serializable {

	Object writeReplace() throws ObjectStreamException;
	
}
