package nl.weeaboo.vn.image.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITextureData;

@CustomSerializable
public abstract class AbstractScreenshot implements IScreenshot {

	private static final long serialVersionUID = ImageImpl.serialVersionUID;

	private final short z;
	private final boolean isVolatile;

	private boolean cancelled;
	private transient ITextureData pixels;
	private Dim screenSize = Dim.EMPTY;

	protected boolean isAvailable;
	private boolean isTransient;

	protected AbstractScreenshot(short z, boolean isVolatile) {
		this.z = z;
		this.isVolatile = isVolatile;
		this.isTransient = isVolatile; // Volatile implies transient
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();

		if (!isTransient && !isVolatile) {
			serializePixels(out, pixels);
		}
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		if (!isTransient && !isVolatile) {
			pixels = deserializePixels(in);
		}
	}

	protected void serializePixels(ObjectOutputStream out, ITextureData pixels) throws IOException {
		out.writeObject(pixels);
	}

	protected ITextureData deserializePixels(ObjectInputStream in) throws IOException, ClassNotFoundException {
		return (ITextureData)in.readObject();
	}

	@Override
	public void markTransient() {
		isTransient = true;
	}

	@Override
	@Deprecated
	public final void makeTransient() {
		markTransient();
	}

	@Override
	public void cancel() {
		cancelled = true;
	}

	@Override
	public boolean isAvailable() {
		return !isCancelled() && isAvailable;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public short getZ() {
		return z;
	}

	@Override
	public final boolean isTransient() {
		return isTransient;
	}

	@Override
	public final boolean isVolatile() {
		return isVolatile;
	}

	@Override
	public ITextureData getPixels() {
		return pixels;
	}

	@Override
	public Dim getScreenSize() {
	    return screenSize;
	}

	protected void setPixels(ITextureData texData, Dim screenSize) {
		this.pixels = Checks.checkNotNull(texData);
		this.screenSize = Checks.checkNotNull(screenSize);
		this.isAvailable = true;
	}

}
