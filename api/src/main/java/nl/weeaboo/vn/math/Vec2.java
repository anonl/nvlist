package nl.weeaboo.vn.math;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import com.google.common.math.DoubleMath;

import nl.weeaboo.common.StringUtil;

public final class Vec2 implements Cloneable, Externalizable {

    public double x, y;

	public Vec2() {
		this(0, 0);
	}
	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Vec2(Vec2 v) {
		this.x = v.x;
		this.y = v.y;
	}

	//Functions
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeDouble(x);
		out.writeDouble(y);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		x = in.readDouble();
		y = in.readDouble();
	}

	@Override
	public Vec2 clone() {
		return new Vec2(x, y);
	}

	@Override
	public String toString() {
		return StringUtil.formatRoot("%s[%.2f, %.2f]",
				getClass().getSimpleName(), x, y);
	}

	@Override
	public int hashCode() {
	    return Arrays.hashCode(new double[] {x, y});
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec2) {
			Vec2 v = (Vec2)obj;
			return equals(v, 0.0);
		}
		return false;
	}

	public boolean equals(Vec2 v, double epsilon) {
		if (epsilon != 0.0) {
			return DoubleMath.fuzzyEquals(x, v.x, epsilon)
					&& DoubleMath.fuzzyEquals(y, v.y, epsilon);
		}

		return x == v.x && y == v.y;
	}

	public void add(Vec2 v) {
		x += v.x;
		y += v.y;
	}
	public void sub(Vec2 v) {
		x -= v.x;
		y -= v.y;
	}
	public void scale(double s) {
		x *= s;
		y *= s;
	}
	public void normalize() {
		scale(1.0 / length());
	}

	public Vec2 cross(Vec2 v) {
		return new Vec2(y - v.y, v.x - x);
	}
	public double dot(Vec2 v) {
		return x*v.x + y*v.y;
	}

	public double lengthSquared() {
		return x*x + y*y;
	}
	public double length() {
		return Math.sqrt(x*x + y*y);
	}

	//Getters

	//Setters

}
