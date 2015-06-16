package nl.weeaboo.lua2.io;

import java.io.ObjectStreamException;
import java.io.Serializable;

public interface IReadResolveSerializable extends Serializable {

	Object readResolve() throws ObjectStreamException;

}
