import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Handles binary serialization and deserialization of a simulation state.
 *
 * <p>Binary format (big-endian):
 * <pre>
 *   int   magic     = 0x43454C4C  ("CELL")
 *   int   version   = 1
 *   int   width
 *   int   height
 *   byte  toroidal  (0 or 1)
 *   int   stepCount
 *   int   agentCount
 *   for each agent:
 *     int    x
 *     int    y
 *     int    stateOrdinal  (0=HEALTHY, 1=INFECTED, 2=RECOVERED, 3=DEAD)
 *     int    age
 *     double energy
 * </pre>
 */
public class SimulationSerializer {

    private static final int MAGIC   = 0x43454C4C;
    private static final int VERSION = 1;

    // Private constructor — static utility class only.
    private SimulationSerializer() {}

    /**
     * Saves the current state of the simulation to a binary file.
     *
     * @param file   destination file
     * @param grid   the simulation grid
     * @param engine the simulation engine (used to save step count and topology)
     * @throws IOException if the file cannot be written
     */
    public static void save(File file, Grid grid, SimulationEngine engine) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            out.writeInt(MAGIC);
            out.writeInt(VERSION);
            out.writeInt(grid.getWidth());
            out.writeInt(grid.getHeight());
            out.writeByte(grid.isToroidal() ? 1 : 0);
            out.writeInt(engine.getCurrentStep());

            java.util.List<Agent> agents = grid.getAllAgents();
            // Also include dead agents still on the grid
            java.util.List<Agent> allOnGrid = new java.util.ArrayList<>();
            for (int x = 0; x < grid.getWidth(); x++) {
                for (int y = 0; y < grid.getHeight(); y++) {
                    Agent a = grid.getAgent(x, y);
                    if (a != null) allOnGrid.add(a);
                }
            }

            out.writeInt(allOnGrid.size());
            for (Agent a : allOnGrid) {
                out.writeInt(a.getPositionX());
                out.writeInt(a.getPositionY());
                out.writeInt(a.getState().ordinal());
                out.writeInt(a.getAge());
                out.writeDouble(a.getEnergy());
            }
        }
    }

    /**
     * Loads a simulation state from a binary file and populates the given grid.
     * The grid is cleared before loading. Returns the saved step count.
     *
     * @param file   source file
     * @param grid   the grid to populate (must match the saved dimensions)
     * @return the step count that was saved
     * @throws IOException              if the file cannot be read or has wrong format
     * @throws IllegalArgumentException if the grid dimensions do not match the file
     */
    public static int load(File file, Grid grid) throws IOException {
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            int magic = in.readInt();
            if (magic != MAGIC)
                throw new IOException("Invalid simulation file (bad magic number).");
            int version = in.readInt();
            if (version != VERSION)
                throw new IOException("Unsupported file version: " + version);

            int width    = in.readInt();
            int height   = in.readInt();
            boolean toro = in.readByte() == 1;
            int step     = in.readInt();

            if (width != grid.getWidth() || height != grid.getHeight())
                throw new IllegalArgumentException(
                    "Grid dimensions in file (" + width + "x" + height +
                    ") do not match current grid (" + grid.getWidth() + "x" + grid.getHeight() + ")."
                );

            grid.clear();
            grid.setToroidal(toro);

            int agentCount = in.readInt();
            HealthState[] states = HealthState.values();
            for (int i = 0; i < agentCount; i++) {
                int x        = in.readInt();
                int y        = in.readInt();
                int ordinal  = in.readInt();
                int age      = in.readInt();
                double energy= in.readDouble();

                Agent a = new Agent(x, y, age);
                a.setEnergy(energy);
                if (ordinal >= 0 && ordinal < states.length)
                    a.setState(states[ordinal]);
                grid.addAgent(a);
            }

            return step;
        }
    }
}
