import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Tree2DTester {
    public static void main(String[] args) throws InterruptedException {
        /*Tree2D<String> tree = new Tree2D<String>();
        tree.put(new Point2D(30, 40), "a");
        tree.put(new Point2D(5, 25), "b");
        tree.put(new Point2D(10, 12), "c");
        tree.put(new Point2D(70, 70), "d");
        tree.put(new Point2D(40, 100), "e");
        tree.put(new Point2D(50, 30), "f");
        tree.put(new Point2D(35, 45), "g");
        tree.put(new Point2D(8, 256), "h");

        //Test reinserting same point...
        tree.put(new Point2D(50, 30), "i");

        //Test inserting things that will compare on equal components when points are unequal...
        tree.put(new Point2D(50, 35), "j");
        tree.put(new Point2D(45, 70), "k");

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
        System.out.println(tree.toString());*/

        int test = 0;
        Random r = new Random();
        while(automatedTesting(r.nextInt(100000), -100000000, 100000000, null)) {
            //Nothing needed!
            test++;
            System.out.println("Running test " + test);
            Thread.sleep(10);
        }

        //automatedTesting(5, -1000, 1000, new Point2D[]{new Point2D(-651.8314f, 616.93713f), new Point2D(-73.53784f, -947.04047f), new Point2D(-479.102f, -490.92816f), new Point2D(-938.8059f, 338.5298f), new Point2D(-17.453064f, 28.254639f)});


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
