package nl.weeaboo.lua2.io;

import java.io.ObjectStreamException;
import java.io.Serializable;

public interface IWriteReplaceSerializable extends Serializable {

	Object writeReplace() throws ObjectStreamException;
	
}
