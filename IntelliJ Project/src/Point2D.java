/*
    Just the class for the keys of Tree2D.
 */

import java.util.Objects;

public class Point2D {
    final float x;
    final float y;

    public Point2D(float x, float y)
    {
        this.x=x;
        this.y=y;
    }

    /*
        Basically compareTo for the x component...
     */
    public int compareX(Point2D other)
    {
        return  Float.compare(this.x, other.x);
    }

    /*
        Bascially compareTo for the y component...
     */
    public int compareY(Point2D other)
    {
        return Float.compare(this.y, other.y);
    }

    public boolean epsEquals(Point2D other)
    {
        return this.distance(other) <= Tree2D.eps;
    }

    public float dot(Point2D other)
    {
        return this.x*other.x + this.y*other.y;
    }

    public float distance(Point2D other)
    {
        return other.subtract(this).magnitude();
    }

    public float magnitude()
    {
        return (float)Math.sqrt(this.dot(this));
    }

    public Point2D add(Point2D other)
    {
        return new Point2D(this.x+other.x, this.y+other.y);
    }

    public Point2D scale(float scalar)
    {
        return new Point2D(scalar*this.x, scalar*this.y);
    }

    public Point2D subtract(Point2D other)
    {
        return this.add(other.scale(-1.0f));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point2D point2D = (Point2D) o;
        return Float.compare(point2D.x, x) == 0 &&
                Float.compare(point2D.y, y) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y);
    }

    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
}
