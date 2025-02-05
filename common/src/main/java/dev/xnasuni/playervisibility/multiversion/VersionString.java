package dev.xnasuni.playervisibility.multiversion;

// OMG someone please help me with javadoc if anyone ever uses this I hope they like don't have a hard time understanding it

public class VersionString {
    private final String operator;
    private final String version;
    private final int[] versionParts;
    private final boolean isSnapshot;
    private int snapshotYear;
    private int snapshotWeek;
    private char snapshotRevision;

    /**
     * Returns a conditioned version object that can be used to test other versions using the <b>test</b> method.
     * <p>The conditioned version string can contain operators such as =, &gt;=, &lt;=, &gt;, or &lt;, followed by the version number with any amount of periods.</p>
     *
     * <p>For example: &gt;=1.21, =1.18.2</p>
     * @param version the raw conditioned version string
     */
    public VersionString(String version) {
        this.operator = version.replaceAll("([<>]=?|=).*", "$1").trim();
        this.version = version.replaceAll("^\\D+", "").trim();
        this.isSnapshot = this.version.matches("\\d+w\\d+[a-z]");
        if (this.isSnapshot) {
            parseSnapshotVersion(this.version);
            this.versionParts = new int[0]; // Not used for snapshots
        } else {
            this.versionParts = parseVersion(this.version);
        }
    }

    /**
     * Returns a conditioned version object that can be used to test other versions using the <b>test</b> method.
     * <p>The conditioned version string can contain operators such as =, &gt;=, &lt;=, &gt;, or &lt;, followed by the version number with any amount of periods.</p>
     *
     * <p>For example: &gt;=1.21, =1.18.2</p>
     * @param version the raw conditioned version string
     * @return a conditioned version that can be used to test other versions using the <b>test</b> method.
     */
    public static VersionString of(String version) {
        return new VersionString(version);
    }

    private int[] parseVersion(String version) {
        String[] parts = version.split("\\.");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        return result;
    }
    private void parseSnapshotVersion(String version) {
        this.snapshotYear = Integer.parseInt(version.substring(0, 2));
        this.snapshotWeek = Integer.parseInt(version.substring(3, 5));
        this.snapshotRevision = version.charAt(5);
    }
    private int compareTo(String otherVersion) {
        VersionString other = new VersionString("=" + otherVersion);
        if (this.isSnapshot && other.isSnapshot) {
            if (this.snapshotYear != other.snapshotYear) return Integer.compare(this.snapshotYear, other.snapshotYear);
            if (this.snapshotWeek != other.snapshotWeek) return Integer.compare(this.snapshotWeek, other.snapshotWeek);
            return Character.compare(this.snapshotRevision, other.snapshotRevision);
        } else if (this.isSnapshot || other.isSnapshot) {
            /*
            FIXME: somehow compare common version A.B.C and snapshot version YYwWWR
            - might just be better to create two lists in versioned mixin annotation for common and snapshot
            */
            // for now let's assume snapshots are always "newer" than regular versions
            return this.isSnapshot ? 1 : -1;
        } else {
            int[] otherParts = other.versionParts;
            int length = Math.min(this.versionParts.length, otherParts.length);
            for (int i = 0; i < length; i++) {
                if (this.versionParts[i] < otherParts[i]) {
                    return -1;
                }
                if (this.versionParts[i] > otherParts[i]) {
                    return 1;
                }
            }
            return this.versionParts.length - otherParts.length;
        }
    }

    /**
     * Compares the current version with the specified version.
     *
     * <p>Returns true if the current conditioned version falls within the range of the parameter otherVersion.</p>
     * <p>For example, a conditioned version looks like '&gt;=1.21.4', and a normal version string looks like '1.21.3'.</p>
     *
     * @param otherVersion The normal version string to compare against. (i.e. 1.21.4)
     * @return true if the current conditioned version falls within the range of the parameter otherVersion; false otherwise.
     */
    public boolean test(String otherVersion) {
        // The >, <, >=, and <= operators flipped intentionally.
        int comparison = compareTo(otherVersion);

//        not using switch for jre version compatibility
//        return switch (this.operator) {
//            case "=" -> comparison == 0;
//            case ">" -> comparison < 0;
//            case "<" -> comparison > 0;
//            case ">=" -> comparison <= 0;
//            case "<=" -> comparison >= 0;
//            default -> throw new IllegalArgumentException("Invalid operator: " + this.operator);
//        };
        if (this.operator.equals("=")) {
            return comparison == 0;
        }
        if (this.operator.equals(">")) {
            return comparison < 0;
        }
        if (this.operator.equals("<")) {
            return comparison > 0;
        }
        if (this.operator.equals(">=")) {
            return comparison <= 0;
        }
        if (this.operator.equals("<=")) {
            return comparison >= 0;
        }
        throw new IllegalArgumentException("Invalid operator: " + this.operator);
    }

    @Override public String toString() {
        return this.operator + this.version;
    }
}