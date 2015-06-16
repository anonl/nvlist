package org.luaj.vm2.lib;

import java.io.InputStream;
import java.io.Serializable;

import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
public class ClassLoaderResourceFinder implements ResourceFinder, Serializable {

	private static final long serialVersionUID = 5304356311433983520L;

    @SuppressWarnings("resource")
    @Override
	public Resource findResource(String filename) {
        if (!filename.startsWith("/")) {
            filename = "/" + filename;
        }

        InputStream in = getClass().getResourceAsStream(filename);
        if (in == null) {
            return null;
        }
		return new Resource(filename, in);
	}

}
