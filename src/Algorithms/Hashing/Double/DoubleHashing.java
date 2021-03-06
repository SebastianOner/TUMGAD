package Algorithms.Hashing.Double;

import Util.Terminal;

import java.util.*;

public class DoubleHashing {
    static StringBuilder doubleHashingExerciseStringBuilder;
    static StringBuilder doubleHashingSolutionStringBuilder;
    /**
     * Template for the solution of a single operation when inserting/deleting number into
     * Hashtable. 3 replacable placeholders and a non-replacable for the next table
     */
    static String tableTemplate = "Operation: \\underline{\\color{tumgadRed}$DHOPERATION$} \\hspace{10px} Position(s): \\underline{\\color{tumgadRed}$DHPOSITIONS$}\n" +
            "        \\begin{center}\n" +
            "            \\begin{tabular}{|P{0.75cm}|P{0.75cm}|P{0.75cm}|P{0.75cm}|P{0.75cm}|P{0.75cm}|P{0.75cm}|P{0.75cm}|P{0.75cm}|P{0.75cm}|P{0.75cm}|}\n" +
            "                \\hline\n" +
            "                0 & 1 & 2 & 3 & 4 & 5 & 6 & 7 & 8 & 9 & 10  \\\\\n" +
            "                \\hline\n" +
            "                $DHTABLEROW$ \\\\\n" +
            "                \\hline\n" +
            "            \\end{tabular}\n" +
            "        \\end{center}\n" +
            "%$DHTABLE$";

    /**
     * generates a first hashfunction like ([1-9]x + 9) mod 11
     */
    public static int[] generateH1Function() {
        Random rand = Terminal.rand;
        int[] a = new int[2];
        a[0] = (rand.nextInt(10) + 1);
        a[1] = (rand.nextInt(10));
        return a;
    }

    /**
     * h2(x) = prime1 - (x % prime2)
     */
    static int[] generateH2Function() {
        int[] primes = {3, 5, 7};
        Random rand = Terminal.rand;
        int[] a = new int[2];
        a[0] = primes[rand.nextInt(3)];
        a[1] = a[0];
        return a;
    }

    public static void generateExercise() {
        doubleHashingExerciseStringBuilder = Terminal.readFile("src/Algorithms/Hashing/Double/DoubleHashingExerciseTemplate.tex");
        doubleHashingSolutionStringBuilder = Terminal.readFile("src/Algorithms/Hashing/Double/DoubleHashingSolutionTemplate.tex");

        int[] hash1 = generateH1Function();
        int[] hash2 = generateH2Function();

        // The next couple of lines generate the numbers the students will have to work with
        int[] numbers = Terminal.generateRandomArray(6, 6);

        int numOfFirstInsertions = Terminal.rand.nextInt(3) + 4;
        int numOfSecondInsertions = 7 - numOfFirstInsertions;
        Integer[] firstInsertions = new Integer[numOfFirstInsertions];

        for (int i = 0; i < numOfFirstInsertions; i++) {
            firstInsertions[i] = numbers[i];
        }

        List<Integer> deletions = new ArrayList<>(Arrays.asList(firstInsertions));
        while (deletions.size() > 3) {
            deletions.remove(Terminal.rand.nextInt(deletions.size()));
        }
        int[] deletionsArr = new int[3];
        for (int i = 0; i < deletions.size(); i++) {
            deletionsArr[i] = deletions.get(i);
        }
        int[] secondInsertions = new int[numOfSecondInsertions];
        int j = 0;
        for (int i = numOfFirstInsertions; i < numbers.length; i++) {
            secondInsertions[j++] = numbers[i];
        }

        // There will always be 1 unfilled position which we fill with the first number we deleted (just 'cause)
        secondInsertions[j] = deletionsArr[0];

        ArrayList<Integer> allInsertions = new ArrayList<>(Arrays.asList(firstInsertions));
        for (int i = 0; i < numOfSecondInsertions; i++) {
            allInsertions.add(secondInsertions[i]);
        }

        generateCollisionTable(new HashSet<Integer>(allInsertions).toArray(new Integer[0]), hash1, hash2);
        generateSteps(hash1, hash2, firstInsertions, deletionsArr, secondInsertions);

        Terminal.replaceinSB(doubleHashingExerciseStringBuilder, "NORMALFUNCTION", "h(x) = (" + hash1[0] + "x + " + hash1[1] + ") \\mod 11");
        Terminal.replaceinSB(doubleHashingSolutionStringBuilder, "NORMALFUNCTION", "h(x) = (" + hash1[0] + "x + " + hash1[1] + ") \\mod 11");

        Terminal.replaceinSB(doubleHashingExerciseStringBuilder, "COLLISIONFUNCTION", "h'(x) = " + hash2[0] + " - (x \\mod " + hash2[1] + ") ");
        Terminal.replaceinSB(doubleHashingSolutionStringBuilder, "COLLISIONFUNCTION", "h'(x) = " + hash2[0] + " - (x \\mod " + hash2[1] + ") ");

        Terminal.replaceinSB(doubleHashingExerciseStringBuilder, "$FIRSTINSERTIONS$", Terminal.printArray(firstInsertions));
        Terminal.replaceinSB(doubleHashingSolutionStringBuilder, "$FIRSTINSERTIONS$", Terminal.printArray(firstInsertions));

        Terminal.replaceinSB(doubleHashingExerciseStringBuilder, "$DELETIONS$", Terminal.printArray(deletionsArr));
        Terminal.replaceinSB(doubleHashingSolutionStringBuilder, "$DELETIONS$", Terminal.printArray(deletionsArr));

        Terminal.replaceinSB(doubleHashingExerciseStringBuilder, "$SECONDINSERTIONS$", Terminal.printArray(secondInsertions));
        Terminal.replaceinSB(doubleHashingSolutionStringBuilder, "$SECONDINSERTIONS$", Terminal.printArray(secondInsertions));

        StringBuilder exerciseStringBuilder = Terminal.readFile("docs/Exercises.tex");
        StringBuilder solutionStringBuilder = Terminal.readFile("docs/Solutions.tex");

        Terminal.replaceinSB(exerciseStringBuilder, "%$DHCELL$", "\\cellcolor{tumgadPurple}");
        Terminal.replaceinSB(solutionStringBuilder, "%$DHCELL$", "\\cellcolor{tumgadRed}");

        Terminal.replaceinSB(exerciseStringBuilder, "%$DOUBLEHASHING$", "\\newpage\n" + doubleHashingExerciseStringBuilder.toString());
        Terminal.replaceinSB(solutionStringBuilder, "%$DOUBLEHASHING$", "\\newpage\n" + doubleHashingSolutionStringBuilder.toString());

        Terminal.saveToFile("docs/Exercises.tex", exerciseStringBuilder);
        Terminal.saveToFile("docs/Solutions.tex", solutionStringBuilder);
    }

    /**
     * Insert a value into a hashtable, ensuring it to get a free space using double hashing
     *
     * @param hashTable the current state of the hashtable the value should be inserted into
     * @param value     the int value to be inserted
     * @param h1        the first hashfunction
     * @param h2        the second hash-function, coming into play if the value collides
     */
    private static void insertToTable(int[] hashTable, int value, int[] h1, int[] h2) {
        int firstHash, hashValue;
        firstHash = hashValue = ((h1[0] * value + h1[1]) % 11);
        String positionString = "" + hashValue;
        int i = 1;
        while (hashTable[hashValue] != -1) {
            hashValue = Math.floorMod((firstHash + i * (h2[0] - Math.floorMod(value, h2[1]))), 11);
            positionString += ", " + hashValue;
            i++;
        }
        hashTable[hashValue] = value;
        Terminal.replaceinSB(doubleHashingSolutionStringBuilder, "%$DHTABLE$", tableTemplate
                .replace("$DHOPERATION$", "Insert(" + value + ")")
                .replace("$DHPOSITIONS$", positionString)
                .replace("$DHTABLEROW$", arrayToRow(hashTable))
        );
    }

    /**
     * see insertFromTable, only with delete
     */
    private static void deleteFromTable(int[] hashTable, int value, int[] h1, int[] h2) {
        int firstHash, hashValue;
        firstHash = hashValue = ((h1[0] * value + h1[1]) % 11);
        String positionString = "" + hashValue;
        int i = 1;
        while (hashTable[hashValue] != value) {
            hashValue = Math.floorMod((firstHash + i * (h2[0] - Math.floorMod(value, h2[1]))), 11);
            positionString += ", " + hashValue;
            i++;
        }
        hashTable[hashValue] = -1;
        Terminal.replaceinSB(doubleHashingSolutionStringBuilder, "%$DHTABLE$", tableTemplate
                .replace("$DHOPERATION$", "Delete(" + value + ")")
                .replace("$DHPOSITIONS$", positionString)
                .replace("$DHTABLEROW$", arrayToRow(hashTable))
        );
    }

    /**
     * generates a LaTeX-friendly row from a provided array
     *
     * @param hashTable the array that should be converted into a LaTeX table row
     */
    private static String arrayToRow(int[] hashTable) {
        String ret = hashTable[0] == -1 ? "" : "" + hashTable[0];
        for (int i = 1; i < hashTable.length; i++) {
            ret += hashTable[i] == -1 ? "&" : "&" + hashTable[i];
        }
        return ret;
    }

    /**
     * generates the separate insertions/deletion steps of the operations (always 10)
     *
     * @param h1               The first hash-function
     * @param h2               The second hash-function, if a collision appears
     * @param firstInsertions  the first couple of values that should be inserted into the hashtable
     * @param deletionsArr     the 3 values that should be deleted after the insertions (a subset of firstInsertions)
     * @param secondInsertions the last couple of values to be inserted
     */
    private static void generateSteps(int[] h1, int[] h2, Integer[] firstInsertions, int[] deletionsArr, int[] secondInsertions) {
        int[] hashTable = new int[11];
        for (int i = 0; i < hashTable.length; i++) {
            hashTable[i] = -1;
        }
        for (int i = 0; i < firstInsertions.length; i++) {
            insertToTable(hashTable, firstInsertions[i], h1, h2);
        }
        for (int i = 0; i < deletionsArr.length; i++) {
            deleteFromTable(hashTable, deletionsArr[i], h1, h2);
        }
        for (int i = 0; i < secondInsertions.length; i++) {
            insertToTable(hashTable, secondInsertions[i], h1, h2);
        }
    }

    /**
     * Generates a collision table, a table that gives you the values to which
     * the numbers in question map to when inserted into the hashfunctions
     * <p>
     * This method generates the table with a width/depth of 5, meaning you can look up
     * the initial hash-value and 4 collision values after
     *
     * @param numbers the numbers that have to be inserted into the table
     * @param h1      first hash function
     * @param h2      second hash function
     */
    private static void generateCollisionTable(Integer[] numbers, int[] h1, int[] h2) {

        for (int i = 0; i < numbers.length; i++) {
            int hash1 = Math.floorMod(h1[0] * numbers[i] + h1[1], 11);
            int hash2 = h2[0] - (numbers[i] % h2[1]);
            String collisionRow = "" + numbers[i] + " & " + hash1 + " & " + hash2;

            int[] collisionHash = new int[6];
            for (int j = 0; j < 6; j++) {
                collisionHash[j] = Math.floorMod((hash1 + j * hash2), 11);
                if (collisionHash[j] < 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                collisionRow += " & " + collisionHash[j];
            }
            Terminal.replaceinSB(doubleHashingSolutionStringBuilder, "%$COLLISIONTABLE$", collisionRow + "\\\\\n\\hline\n%$COLLISIONTABLE$");
        }
    }
}
