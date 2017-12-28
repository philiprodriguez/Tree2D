public class Point2DPair<DataType> {
    private final Point2D point;
    private final DataType data;

    public Point2DPair(Point2D point, DataType data)
    {
        this.point = point;
        this.data = data;
    }

    public Point2D getPoint()
    {
        return point;
    }

    public DataType getData()
    {
        return data;
    }

    public String toString()
    {
        return "[" + getPoint().toString() + ", " + getData().toString() + "]";
    }
}
