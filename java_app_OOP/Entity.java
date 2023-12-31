package towerwarspp.board;

import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Position;
import towerwarspp.util.debug.Debug;

import java.util.HashSet;
import java.util.Vector;

import static towerwarspp.util.debug.DebugLevel.LEVEL_1;
import static towerwarspp.util.debug.DebugSource.BOARD;

/**
 * This class represents a token in the TowerWars game. Every {@link Entity} object contains information about
 * its current position on the board, height, possible moves, if it is blocked, and if it is a base (has height 0 and no possible moves).
 * Each {@link Entity} object representing a simple stone can become a tower. But if the object represents a base, the property of being a base
 * can not be changed. Additionally, every {@link Entity} has information about the board size, the maximal allowed tower height,
 * and the maximal possible step range.
 *
 * @author Anastasiia Kysliak
 * @version 15-07-2017
 */
public class Entity {

    /**
     * The color of the Entity.
     */
    private final PlayerColor color;

    /**
     * The maximum allowed height.
     */
    private final int maxHeight;

    /**
     * The maximum step range which a token on the board of the given size can have.
     */
    private final int maxRange;

    /**
     * The Position of the Entity on the board.
     */
    private Position position;

    /**
     * The height of the Entity. If height == 0, this entity represents a normal stone. If height > 0, it is a tower.
     */
    private int height;

    /**
     * Shows whether the Entity is blocked or not.
     */
    private boolean isBlocked = false;

    /**
     * Represents the current step range of the Entity.
     */
    private int range = 1;

    /**
     * Contains all possible moves of this entity sorted according to the step range needed for reaching the end position.
     * This means: the position n in the Vector contains a reference to the HashSet where all possible moves of the range n are stored.
     */
    private Vector<HashSet<Move>> allMoves;

    /**
     * The size of the board.
     */
    private int size;

    /**
     * Indicates if this entity is a base or not: if true, this entity is a base.
     */
    private boolean isBase = false;

    /**
     * counts, how many moves this entity has
     */
    private int moveCounter = 0;

    /**
     * Instance of the class {@link Debug}.
     */
    private Debug debug;

    /**
     * Constructor for the {@link Entity} object. creates a new Entity which represents a stone: has the height 0 and the range 1.
     *
     * @param pos   the current position of this token.
     * @param color the color of this token.
     * @param size  the size of the board on which this token will be used for the TowerWars game.
     */
    public Entity(Position pos, PlayerColor color, int size) {
        this.position = pos;
        this.color = color;
        this.size = size;
        maxHeight = size / 3;
        maxRange = 6 * maxHeight + 2;
        this.debug = Debug.getInstance();
        initialiseMoves();
    }

    /**
     * Constructor for for the {@link Entity} object which has to be used in order to create a base:
     * it allowes to set the variable isBase on true.
     *
     * @param pos    the current position of this token.
     * @param color  the color of this token.
     * @param size   the size of the board on which this token will be used for the TowerWars game.
     * @param isBase defines if the new token has to be a base or a normal stone.
     */
    public Entity(Position pos, PlayerColor color, int size, boolean isBase) {
        this(pos, color, size);
        this.isBase = isBase;
    }

    /**
     * Copy-Constructor: creates a new {@link Entity} object which is a copy of the given {@link Entity} object original.
     *
     * @param original {@link Entity} object which has to be copied.
     */
    public Entity(Entity original) {
        this.position = original.position;
        this.color = original.color;
        this.height = original.height;
        this.size = original.size;
        this.range = original.range;
        this.isBlocked = original.isBlocked;
        this.isBase = original.isBase;
        this.moveCounter = original.moveCounter;
        this.maxHeight = original.maxHeight;
        this.maxRange = original.maxRange;
        this.allMoves = original.copyAllMoves();
        this.debug = Debug.getInstance();
    }

    /**
     * Returns the current position of this entity.
     *
     * @return the current position of this entity.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Changes the current position of this entity.
     *
     * @param pos the new position.
     */
    public void setPosition(Position pos) {
        this.position = pos;
    }

    /**
     * Returns the color of this entity.
     *
     * @return the color of this entity.
     */
    public PlayerColor getColor() {
        return color;
    }

    /**
     * Returns the height of this entity.
     *
     * @return the height of this entity.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the current step range of this entity.
     *
     * @return the current step range of this entity.
     */
    public int getRange() {
        return range;
    }

    /**
     * Returns a reference to the collection of all possible moves stored in this {@link Entity} object (does not copy or restore the moves).
     *
     * @return a reference to the collection of all possible moves stored in this {@link Entity} object.
     */
    public Vector<HashSet<Move>> getMoves() {
        return allMoves;
    }

    /**
     * Returns all possible moves of this Entity stored in a vector.
     *
     * @return all possible moves of this Entity stored in a vector.
     */
    public Vector<Move> getMovesAsVector() {
        Vector<Move> moves = new Vector<Move>();
        if (!isBlocked) {
            for (int i = 1; i <= range; ++i) {
                moves.addAll(allMoves.get(i));
            }
        }
        return moves;
    }

    /**
     * Returns true if this entity is blocked.
     *
     * @return true if this entity is blocked.
     */
    public boolean isBlocked() {
        return isBlocked;
    }

    /**
     * Sets the Variable isBlocked to the specified value.
     *
     * @param blocked defines if this entity representing a tower has to be blocked or unblocked.
     */
    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
    }

    /**
     * Returns true if this entity is a base.
     *
     * @return true if this entity is a base.
     */
    public boolean isBase() {
        return isBase;
    }

    /**
     * Returns the moveCounter.
     *
     * @return moveCounter.
     */
    public int getMoveCounter() {
        return moveCounter;
    }

    /**
     * Returns true if this entity is a tower.
     *
     * @return true if this entity is a tower (its height > 0).
     */
    public boolean isTower() {
        return height > 0;
    }

    /**
     * Returns true if this entity is a tower of the maximum height.
     *
     * @return true if this entity is a tower of the maximum height.
     */
    public boolean isMaxHeight() {
        return height == maxHeight;
    }

    /**
     * Returns true if this entity can be moved. This means: this entity is not a base, not blocked and has at least one possible move.
     *
     * @return true if this entity can be moved.
     */
    public boolean isMovable() {
        if (isBase || isBlocked) {
            return false;
        }
        return moveCounter > 0;

    }

    /**
     * Returns true if this entity a move to the specified position.
     *
     * @param end   position of the move in question.
     * @param range distance to the move's end position from the current position of this entity.
     */
    synchronized public boolean hasMove(Position end, int range) {
        return allMoves.get(range).contains(new Move(position, end));
    }
//=============_CHANGED:_+EXCEPTIN=============================
    /**
     * Increases the height of this entity by one.
     */
    public void incHeight() {
        ++height;
        if (height > maxHeight) {
            debug.send(LEVEL_1, BOARD, "Tower has height > maxHeight");
		throw new IllegalStateException("Tower has height > maxHeight");
        }
    }
//=============_CHANGED:_+EXCEPTIN=============================
    /**
     * Decreases the height of this entity by one.
     */
    public void decHeight() {
        --height;
        if (height < 0) {
            debug.send(LEVEL_1, BOARD, "Entity has height < 0");
		throw new IllegalStateException("Tower has height > maxHeight");
        }
    }
//=============_CHANGED:_+EXCEPTIN=============================
    /**
     * Decreases the step range of this entity by one and removes all moves
     * which are not possible anymore
     */
    synchronized public void decRange() {
        moveCounter -= allMoves.get(range).size();
        allMoves.set(range, new HashSet<Move>());
        --range;
        if (range < 0) {
            debug.send(LEVEL_1, BOARD, "range < 0");
		throw new IllegalStateException("range < 0"); 
        }
    }

//=============_CHANGED:_+EXCEPTIN=============================
    /**
     * Increases the step range of this entity by one. The newly possible moves have to be added extra.
     */
    public void incRange() {
        ++range;
        if (range > maxRange) {
            debug.send(LEVEL_1, BOARD, "range > maxRange");
		throw new IllegalStateException("range > maxRange"); 
        }
    }

    /**
     * Creates a new move to the position end add adds it to all possible moves of this entity if there was no such move.
     *
     * @param end   end position of the move in question.
     * @param range distance to the move's end position from the current position of this entity.
     */
    synchronized public void addMove(Position end, int range) {
        if (range != 0 && allMoves.get(range).add(new Move(position, end))) {
            ++moveCounter;
        }
    }

    /**
     * Removes the move to the specified position from the collection of all posiible moves if this entity had such move.
     *
     * @param end   end position of the move in question.
     * @param range distance to the move's end position from the current position of this entity.
     */
    synchronized public void removeMove(Position end, int range) {
        if (range != 0 && allMoves.get(range).remove(new Move(position, end))) {
            --moveCounter;
        }
    }

    /**
     * Removes all moves from the collection of all possible moves and sets the range to 1.
     */
    public void removeAllMoves() {
        initialiseMoves();
    }

    /**
     * Returns collection of clones of all possible moves.
     *
     * @return collection of clones of all possible moves.
     */
    synchronized private Vector<HashSet<Move>> copyAllMoves() {
        Vector<HashSet<Move>> clone = new Vector<HashSet<Move>>();
        for (HashSet<Move> set : allMoves) {
            clone.add(new HashSet<Move>(set));
        }
        return clone;
    }

    /**
     * Initializes the collection of all possible moves.
     */
    synchronized private void initialiseMoves() {
        allMoves = new Vector<HashSet<Move>>(maxRange);
        for (int i = 0; i < maxRange; ++i) {
            allMoves.add(i, new HashSet<Move>(i * 6 + 1));
        }
        moveCounter = 0;
        this.range = 1;
    }

    /**
     * Clones this {@link Entity} instance with the copy-constructor.
     *
     * @return the clone of this entity.
     */
    @Override
    synchronized public Entity clone() {
        return new Entity(this);
    }

//============================ADDED=============================================
	public HashSet<Move> getRangeMoves(int range) {
		return allMoves.get(range);
	}
  /**
     * Increases the step range of this entity by one. The newly possible moves have to be added extra.
     */
    public void incRange(HashSet<Move> newMoves) {
        ++range;
        if (range > maxRange) {
            debug.send(LEVEL_1, BOARD, "range > maxRange");
		throw new IllegalStateException("range > maxRange"); 
        }
	allMoves.set(range, newMoves);
    }
	public void setAllMoves(Vector<HashSet<Move>> newMoves, int newRange, int newMoveCounter) {
		allMoves = newMoves;
		range = newRange;
		moveCounter = newMoveCounter;
	}
}
