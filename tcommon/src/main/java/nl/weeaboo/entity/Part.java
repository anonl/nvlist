package nl.weeaboo.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Part implements Serializable {

	private static final long serialVersionUID = 1L;

	transient int refcount;
	transient World world;

	public Part() {
	}

	public String toDetailedString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append("(");
		int t = 0;
		for (Field field : getClass().getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers)) {
				continue;
			}

			if (t > 0) sb.append(", ");

			sb.append(field.getName()).append("=");
			try {
				field.setAccessible(true);
				Object val = field.get(this);
				sb.append(val);
			} catch (Exception e) {
				EntityLog.d("Exception while trying to access Part." + field.getName() + " using reflection", e);
				sb.append("?");
			}
			t++;
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * This method is called whenever this part becomes attached to a world.
	 * @param w The world this part is now attached to.
	 */
	public void onAttached(World w) {
	}

	/**
	 * This method is called whenever this part becomes detached to a world.
	 * @param w The world this part is now detached from.
	 */
	public void onDetached(World w) {
	}

	/**
	 * @param signal The signal which is optionally handled by this part.
	 */
	public void handleSignal(ISignal signal) {
	}

	protected void firePropertyChanged(String propertyName, Object newValue) {
		if (world != null) {
			world.firePartPropertyChanged(this, propertyName, newValue);
		}
	}

}
