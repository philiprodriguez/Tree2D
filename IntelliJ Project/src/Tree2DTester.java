import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Tree2DTester {
    public static void main(String[] args) throws InterruptedException {
        Tree2D<Integer> tree = new Tree2D<Integer>();
        tree.put(new Point2D(30, 40), 1);
        tree.put(new Point2D(5, 25), 2);
        tree.put(new Point2D(10, 12), 3);
        tree.put(new Point2D(70, 70), 4);
        tree.put(new Point2D(40, 100), 5);
        tree.put(new Point2D(50, 30), 6);
        tree.put(new Point2D(35, 45), 7);
        tree.put(new Point2D(8, 256), 8);

        //Test reinserting same point...
        tree.put(new Point2D(50, 30), 9);

        //Test inserting things that will compare on equal components when points are unequal...
        tree.put(new Point2D(50, 35), 10);
        tree.put(new Point2D(45, 70), 11);

        System.out.println(tree.toString());
        System.out.println(tree.size());
        System.out.println(tree.getAll());

        //Test getting!
        System.out.println("Should NOT be null:");
        for(Point2DPair pair : tree.getAll())
        {
            System.out.println("Searching for " + pair.getPoint() + ": " + tree.get(pair.getPoint()));
        }
        System.out.println();
        System.out.println("Should be null:");
        System.out.println("Searching for " + new Point2D(5, 2) + ": " + tree.get(new Point2D(5, 2)));
        System.out.println("Searching for " + new Point2D(15, 27) + ": " + tree.get(new Point2D(15, 27)));

        //Removal testing
        tree.remove(new Point2D(70, 70));

        System.out.println(tree.size());
        System.out.println(tree.toString());

        //Range query

        System.out.println(tree.get2DRange(new Point2D(5, 25), new Point2D(45, 70)));




        /*
            This little section is for infinite automated testing... it'll go till it breaks!
         */
        /*
        int test = 0;
        Random r = new Random();
        while(automatedTesting(r.nextInt(100000), -100000000, 100000000, null)) {
            //Nothing needed!
            test++;
            System.out.println("Running test " + test);
            Thread.sleep(10);
        }
        */

        doTheInsertionAndQuery(tree, 50000000);


    }

    public static void doTheInsertionAndQuery(Tree2D<Integer> tree, int numInsertions)
    {
        Random r = new Random();
        System.out.println("Inserting " + numInsertions + " points...");
        long start = System.currentTimeMillis();
        for(int i = 0; i < numInsertions; i++)
        {
            Point2D point = new Point2D(-1000+(r.nextFloat()*2000), -1000+(r.nextFloat()*2000));
            //System.out.println("Inserting point " + i + "; " + point);
            tree.put(point, r.nextInt());
        }
        long end = System.currentTimeMillis();
        System.out.println("Tree size is " + tree.size());
        System.out.println("Insertion took " + (end-start) + "ms, or " + (((end-start)*1000)/(double)numInsertions) + "microseconds per insertion.");
        ArrayList<Point2DPair<Integer>> results = tree.get2DRange(new Point2D(5, 25), new Point2D(45, 70));
        start = System.currentTimeMillis();
        System.out.println("Range query found " + results.size() + " results.");
        end = System.currentTimeMillis();
        System.out.println("Range query took " + (end-start) + "ms.");
    }

    /*
        This method should insert a bunch of things, make sure they can be found, and remove a bunch of things, etc.
     */
    public static final boolean printTrees = false;
    public static final boolean verifyAfterDeletion = true;
    public static boolean automatedTesting(int numElements, float minVal, float maxVal, Point2D[] forcedElements)
    {
        System.out.println("Inserting points...");
        Random r = new Random();

        ArrayList<Point2D> pointsBackup = new ArrayList<>();
        HashMap<Point2D, Long> pointsMap = new HashMap<Point2D, Long>();
        Tree2D<Long> pointsTree = new Tree2D<Long>();


        for(int i = 0; i < numElements; i++)
        {
            Point2D point;
            if (forcedElements == null)
            {
                point = new Point2D(minVal+(r.nextFloat()*(maxVal-minVal)), minVal+(r.nextFloat()*(maxVal-minVal)));
            }
            else
            {
                point = forcedElements[i];
            }

            if (pointsMap.containsKey(point))
            {
                System.out.println("Skipping insertion of duplicate value " + point);
                i--;
                continue;
            }

            long data = r.nextLong();
            System.out.println("Inserting " + point + " with data " + data);
            pointsBackup.add(point);
            pointsMap.put(point, data);
            pointsTree.put(point, data);
        }

        if(printTrees)
        {
            System.out.println("Current tree:");
            System.out.println(pointsTree.toString());
        }

        System.out.println("Verifying size. Size of pointsTree is " + pointsTree.size() + ". Size of pointsMap is " + pointsMap.size() + ".");
        if (pointsTree.size() != pointsMap.size())
        {
            System.err.println("Size mismatch!");
            return false;
        }
        else
        {
            System.out.println("Sizes match! Good!");
        }

        /*
            Now we're going to remove each element one by one and after each removal verify that all other points can
            be found and out removed points cannot be found!
         */
        ArrayList<Point2D> pointsList = new ArrayList<>(pointsMap.keySet());
        ArrayList<Point2D> removedList = new ArrayList<>();

        while(pointsMap.size() > 0)
        {
            //Remove a random point from our things...
            int selection = r.nextInt(pointsList.size());
            System.out.println("Removing " + pointsList.get(selection) + " from tree of size " + pointsTree.size() + "... ");

            //Remove it from both!
            pointsMap.remove(pointsList.get(selection));
            pointsTree.remove(pointsList.get(selection));
            removedList.add(pointsList.get(selection));
            pointsList.remove(selection);

            if(printTrees)
            {
                System.out.println("Current tree:");
                System.out.println(pointsTree.toString());
            }

            if (pointsMap.size() != pointsTree.size()) {
                System.err.println("Size verification failed. Size of pointsMap is " + pointsMap.size() + ", and size of pointsTree is " + pointsTree.size());
                printListNicely(pointsBackup);
                return false;
            }
            if (verifyAfterDeletion) {
                System.out.print("Verifying content after removal... ");

                //Re-verify all contents...
                for (Point2D point : pointsList) {
                    if (pointsMap.get(point) == null) {
                        System.err.println("HashMap lookup failed for point " + point + ". Should never happen!!!");
                        printListNicely(pointsBackup);
                        return false;
                    }
                    if (!pointsMap.get(point).equals(pointsTree.get(point))) {
                        System.err.println("Verification failed for point " + point + ". HashMap returns " + pointsMap.get(point) + ", but Tree2D returns " + pointsTree.get(point));
                        printListNicely(pointsBackup);
                        return false;
                    }
                }
                for (Point2D point : removedList) {
                    if (pointsMap.get(point) != null) {
                        System.err.println("HashMap lookup found point " + point + " when it should have been removed. Should never happen!!!");
                        printListNicely(pointsBackup);
                        return false;
                    }
                    if (pointsTree.get(point) != null) {
                        System.err.println("Tree2D returned " + pointsTree.get(point) + " for " + point + " when it shoul dhave been removed!");
                        printListNicely(pointsBackup);
                        return false;
                    }
                }

                System.out.println("OK");
            }
            else
            {
                System.out.println("Verification step skipped!");
            }
        }

        System.out.println("Automated testing completed successfully!");
        return true;
    }

    public static void printListNicely(ArrayList<Point2D> list)
    {
        System.out.println();
        System.out.println("List Content:");
        System.out.print("{");
        for(int i = 0; i < list.size(); i++)
        {
            System.out.print("new Point2D(" + list.get(i).x + "f, " + list.get(i).y + "f)");
            if (i != list.size()-1)
                System.out.print(", ");
        }
        System.out.println("}");
    }
}
