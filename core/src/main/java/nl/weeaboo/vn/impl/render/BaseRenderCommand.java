package nl.weeaboo.vn.impl.render;

import nl.weeaboo.vn.core.BlendMode;

class BaseRenderCommand extends RenderCommand {

    public final short z;
    public final boolean clipEnabled;
    public final BlendMode blendMode;

    /** ARGB8888, unassociated alpha */
    public final int argb;

    protected BaseRenderCommand(byte id, short z, boolean clipEnabled,
            BlendMode blendMode, int argb, byte privateField) {

        super(id, ((-(1 + z))                << 16) //We have to be careful, -Short.MIN_VALUE == Short.MIN_VALUE!!!
                | ((clipEnabled ? 1 : 0)     << 15)
                | ((blendMode.ordinal() & 7) << 12)
                | ((id & 15)                 << 8 )
                | (privateField & 255));

        this.z = z;
        this.clipEnabled = clipEnabled;
        this.blendMode = blendMode;
        this.argb = argb;
    }

    protected BaseRenderCommand(byte id, short z, boolean clipEnabled, byte privateField) {
        this(id, z, clipEnabled, BlendMode.DEFAULT, 0xFFFFFFFF, privateField);
    }

}
