
/*
    A class by Philip Rodriguez in December 2017 for a kd tree map of dimension 2.
    It maps a 2D point to an object.

    It is supposed to also be thread safe.

    Even depths split on x, odd depths slit on y. Equality resolves to the right.
 */


import java.util.ArrayList;

public class Tree2D<DataType> {
    public static final float eps = 0.000001f;

    private volatile Tree2DNode<DataType> rootNode;
    private final Object treeLock;

    private volatile long size;
    private final Object sizeLock;

    /*
        Create an empty tree.
     */
    public Tree2D()
    {
        rootNode = null;
        size = 0;
        sizeLock = new Object();
        treeLock = new Object();
    }

    /*
        This should construct a balanced tree using startingData.
     */
    public Tree2D(ArrayList<DataType> startingData)
    {
        throw new IllegalStateException("Not yet implemented!");
    }

    public long size()
    {
        return size;
    }

    /*
        Locks size and then increments it!
     */
    private void incrementSize()
    {
        synchronized (sizeLock)
        {
            size++;
        }
    }

    /*
        Locks size and then decrements it!
     */
    private void decrementSize()
    {
        synchronized (sizeLock)
        {
            size--;
        }
    }

    /*
        Returns true iff the tree is empty.
     */
    public boolean isEmpty()
    {
        return rootNode == null;
    }


    /*
        Find or insert node with key and put at that node the data value.
     */
    public void put(Point2D key, DataType value)
    {
        if (isEmpty())
        {
            rootNode = new Tree2DNode<DataType>(key, value);
            incrementSize();
        }
        else
        {
            //Find info on our node...
            NodePair<DataType> nodePair = findNodeRec(null, rootNode, key, 0);

            if (nodePair.getNode() != null)
            {
                //The node already existed in the tree and we found it...
                nodePair.getNode().setData(value);
            }
            else
            {
                //No such node exists in the tree yet... but we got the parent of where the new node belongs...
                //Note depth-1 since depth-1 is the depth of the parent node!
                int comparison;
                if ((nodePair.getDepth()-1) % 2 == 0)
                {
                    //Compare X
                    comparison = key.compareX(nodePair.getParentNode().getPoint());
                }
                else
                {
                    //Compare Y
                    comparison = key.compareY(nodePair.getParentNode().getPoint());
                }

                if (comparison < 0)
                {
                    //Go left
                    nodePair.getParentNode().setLeft(new Tree2DNode<DataType>(key, value));
                }
                else
                {
                    //Go right
                    nodePair.getParentNode().setRight(new Tree2DNode<DataType>(key, value));
                }
                incrementSize();
            }
        }
    }

    /*
        Remove node with this key!
     */
    public void remove(Point2D key)
    {
        removeNode(null, rootNode, key, 0);
    }

    /*
        Remove the node with key [key] in the tree rooted at [curNode] which has depth [depth].
     */
    private void removeNode(Tree2DNode<DataType> parentNode, Tree2DNode<DataType> curNode, Point2D key, int depth)
    {
        if (curNode == null)
        {
            //No point to delete! We're done!
            return;
        }

        if (curNode.getPoint().epsEquals(key))
        {
            //We found our node to delete!

            if (curNode.getLeft() == null && curNode.getRight() == null)
            {
                //Leaf node
                //Are we root?
                if (parentNode == null)
                {
                    //Yep, we're root!
                    rootNode = null;
                }
                else
                {
                    //Not root!
                    parentNode.nullChild(curNode);
                }
                decrementSize();
            }
            else if (curNode.getRight() != null)
            {
                //Has right child
                Tree2DNode<DataType> minNodeInRight;
                if (depth % 2 == 0)
                {
                    //Splitting on X
                    minNodeInRight = findMin(curNode.getRight(), depth+1, 'x');
                }
                else
                {
                    //Splitting on Y
                    minNodeInRight = findMin(curNode.getRight(), depth+1, 'y');
                }

                curNode.setPoint(minNodeInRight.getPoint());
                curNode.setData(minNodeInRight.getData());
                removeNode(curNode, curNode.getRight(), minNodeInRight.getPoint(), depth+1);
            }
            else
            {
                //Has left child
                Tree2DNode<DataType> minNodeInLeft;
                if (depth % 2 == 0)
                {
                    minNodeInLeft = findMin(curNode.getLeft(), depth+1, 'x');
                }
                else
                {
                    minNodeInLeft = findMin(curNode.getLeft(), depth+1, 'y');
                }

                curNode.setPoint(minNodeInLeft.getPoint());
                curNode.setData(minNodeInLeft.getData());
                removeNode(curNode, curNode.getLeft(), minNodeInLeft.getPoint(), depth+1);

                curNode.setRight(curNode.getLeft());
                curNode.setLeft(null);
            }
        }
        else
        {
            if (depth % 2 == 0)
            {
                //Split on x
                if (key.compareX(curNode.getPoint()) < 0)
                {
                    //Go left
                    removeNode(curNode, curNode.getLeft(), key, depth+1);
                }
                else
                {
                    //Go right
                    removeNode(curNode, curNode.getRight(), key, depth+1);
                }
            }
            else
            {
                //Split on y
                if (key.compareY(curNode.getPoint()) < 0)
                {
                    //Go left
                    removeNode(curNode, curNode.getLeft(), key, depth+1);
                }
                else
                {
                    //Go right
                    removeNode(curNode, curNode.getRight(), key, depth+1);
                }
            }
        }
    }

    /*
        Return the minimum value node...
     */
    private Tree2DNode<DataType> findMin(Tree2DNode<DataType> curNode, int depth, char x_or_y)
    {
        if (curNode == null)
            return null;

        if (((depth % 2 == 0) && (x_or_y == 'x')) || ((depth % 2 == 1) && (x_or_y == 'y')))
        {
            //Agreement between our curNode's split dimension and x_or_y
            //Thus, we want to minimize the dimension, so go left!

            if (curNode.getLeft() == null)
            {
                return curNode;
            }
            else
            {
                return findMin(curNode.getLeft(), depth+1, x_or_y);
            }
        }
        else
        {
            //Disagreement... so we must try both directions!

            Tree2DNode<DataType> minInLeft = findMin(curNode.getLeft(), depth+1, x_or_y);
            Tree2DNode<DataType> minInRight = findMin(curNode.getRight(), depth+1, x_or_y);

            if (minInLeft == null && minInRight == null)
            {
                //No kids, so I am the min!
                return curNode;
            }
            if (minInLeft == null)
            {
                return Tree2DNode.min(curNode, minInRight, x_or_y);
            }
            else if (minInRight == null)
            {
                return Tree2DNode.min(curNode, minInLeft, x_or_y);
            }
            else
            {
                //Both not null!
                return Tree2DNode.min(Tree2DNode.min(curNode, minInLeft, x_or_y), minInRight, x_or_y);
            }

        }
    }

    /*
        Get the data at key and return it, or null if no node exists with key.
     */
    public DataType get(Point2D key)
    {
        NodePair<DataType> nodePair = findNodeRec(null, rootNode, key, 0);
        if (nodePair.getNode() != null)
            return nodePair.getNode().getData();
        else
            return null;
    }

    /*
        This method is the heart and sould of many methods in this class! Make sure it works!

        This method searches the tree rooted at curNode and returns the following:

        A NodePair object containing:
             -a Tree2DNode [node] which is the node in the tree whose key is [key], or null if no such node exists
             in the tree
            -a Tree2DNode [parentNode] which is the node in the tree whose child should be a node whose key is [key], or
            null if no such parent exists (empty tree)
            -An integer [depth] which is the depth of the node whose key is [key].
     */
    private NodePair<DataType> findNodeRec(Tree2DNode<DataType> parentNode, Tree2DNode<DataType> curNode, Point2D key, int depth)
    {
        if (curNode == null)
            return new NodePair<DataType>(null, null, 0);
        if (curNode.getPoint().epsEquals(key))
            return new NodePair<DataType>(parentNode, curNode, depth);

        //Do we go left or right?

        //Compare either based on X or Y depending on depth...
        int comparison;
        if (depth%2 == 0)
            comparison = key.compareX(curNode.getPoint());
        else
            comparison = key.compareY(curNode.getPoint());

        if (comparison < 0)
        {
            //Go left
            if (curNode.getLeft() == null)
            {
                //Return parent only....
                return new NodePair<DataType>(curNode, null, depth+1);
            }
            else {
                return findNodeRec(curNode, curNode.getLeft(), key, depth+1);
            }
        }
        else
        {
            //Go right
            if (curNode.getRight() == null)
            {
                //Return parent only....
                return new NodePair<DataType>(curNode, null, depth+1);
            }
            else
            {
                return findNodeRec(curNode, curNode.getRight(), key, depth+1);
            }
        }
    }

    /*
        Get all points (and data) in the range of the given from and to points.
     */
    public ArrayList<Point2DPair> get2DRange(Point2D from, Point2D to)
    {
        throw new IllegalStateException("Not yet implemented!");
    }

    /*
        Returns an ArrayList containing all keys and values in nice Point2DPair format.
     */
    public ArrayList<Point2DPair> getAll()
    {
        ArrayList<Point2DPair> all = new ArrayList<>();
        populateAll(rootNode, all);
        return all;
    }

    /*
        Recursive method to populate ArrayList all with everything in the tree rooted at curNode.
     */
    private void populateAll(Tree2DNode curNode, ArrayList<Point2DPair> all)
    {
        if (curNode == null)
            return;

        populateAll(curNode.getLeft(), all);
        all.add(curNode.getPoint2DPair());
        populateAll(curNode.getRight(), all);
    }


    /*
        Return a text treeview using spaces.
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        stringTreeRec(rootNode, 0, sb);
        return sb.toString();
    }

    /*
        A recursive helper method to build up the content of sb...
     */
    private void stringTreeRec(Tree2DNode<DataType> curNode, int depth, StringBuilder sb)
    {
        if (curNode == null)
            return;

        for(int i = 0; i < depth; i++)
            sb.append("    ");
        sb.append(curNode.toString());
        sb.append("\n");
        if (curNode.getLeft() != null) {
            sb.append("L: ");
            stringTreeRec(curNode.getLeft(), depth + 1, sb);
        }
        if (curNode.getRight() != null) {
            sb.append("R: ");
            stringTreeRec(curNode.getRight(), depth + 1, sb);
        }
    }

    private static class Tree2DNode<DataType>
    {
        private volatile Point2D point;
        private volatile DataType data;
        private volatile Tree2DNode<DataType> left;
        private volatile Tree2DNode<DataType> right;

        private Tree2DNode(Point2D point, DataType data)
        {
            setPoint(point);
            setData(data);
        }

        private synchronized Tree2DNode<DataType> getLeft()
        {
            return left;
        }

        private synchronized Tree2DNode<DataType> getRight()
        {
            return right;
        }

        private synchronized void setLeft(Tree2DNode<DataType> left)
        {
            this.left = left;
        }

        private synchronized void setRight(Tree2DNode<DataType> right)
        {
            this.right = right;
        }

        private synchronized void nullChild(Tree2DNode<DataType> child)
        {
            if (getLeft() != null && getLeft().getPoint().epsEquals(child.getPoint()))
            {
                setLeft(null);
            }
            else if (getRight() != null)
            {
                setRight(null);
            }
        }

        private synchronized void setData(DataType data)
        {
            this.data = data;
        }

        private synchronized DataType getData()
        {
            return data;
        }

        private synchronized void setPoint(Point2D point)
        {
            this.point = point;
        }

        private synchronized Point2D getPoint()
        {
            return point;
        }

        private synchronized Point2DPair getPoint2DPair()
        {
            return new Point2DPair<DataType>(getPoint(), getData());
        }

        /*
            Return the node whose point is smaller in the dimension of x_or_y
         */
        private static <DataType> Tree2DNode<DataType> min(Tree2DNode<DataType> a, Tree2DNode<DataType> b, char x_or_y)
        {
            if (x_or_y == 'x')
            {
                if (a.getPoint().x <= b.getPoint().x)
                    return a;
                else
                    return b;
            }
            else
            {
                if (a.getPoint().y <= b.getPoint().y)
                    return a;
                else
                    return b;
            }
        }

        public String toString()
        {
            return "[" + getPoint().toString() + ", " + getData().toString() + "]";
        }
    }

    private static class NodePair<DataType>
    {
        private final Tree2DNode<DataType> parentNode;
        private final Tree2DNode<DataType> node;
        private final int depth;

        private NodePair(Tree2DNode<DataType> parentNode, Tree2DNode<DataType> node, int depth)
        {
            this.parentNode = parentNode;
            this.node = node;
            this.depth = depth;
        }

        private Tree2DNode<DataType> getParentNode()
        {
            return parentNode;
        }

        private Tree2DNode<DataType> getNode()
        {
            return node;
        }

        private int getDepth()
        {
            return depth;
        }

        public String toString()
        {
            return "[" + node.toString() + ", depth=" + depth  + "]";
        }
    }
}


