package hw3;

import static api.Direction.*;
import static api.Orientation.*;

import java.util.ArrayList;

import api.Cell;
import api.CellType;
import api.Direction;
import api.Move;

/**
 * Represents a board in the Block Slider game. A board contains a 2D grid of
 * cells and a list of blocks that slide over the cells.
 */
public class Board
{
	/**
	 * 2D array of cells, the indexes signify (row, column) with (0, 0) representing
	 * the upper-left cornner of the board.
	 */
	private Cell[][] grid;

	/**
	 * A list of blocks that are positioned on the board.
	 */
	private ArrayList<Block> blocks;

	/**
	 * A list of moves that have been made in order to get to the current position
	 * of blocks on the board.
	 */
	private ArrayList<Move> moveHistory;

	/**
	 * Variable to hold the grabbed block.
	 */
	private Block grabbedBlock;

	/**
	 * Variable to hold the grabbed cell.
	 */
	private Cell grabbedCell;

	/**
	 * Keeps track of the total number of moves done.
	 */
	private int totMoves;

	/**
	 * Boolean to check whether the game has ended or not.
	 */
	private boolean isGameEnded;

	/*
	 * A list of all legal moves for a block, from its fixed position.
	 */
	private ArrayList<Move> allPossibleMoves;

	/**
	 * Constructs a new board from a given 2D array of cells and list of blocks. The
	 * cells of the grid should be updated to indicate which cells have blocks
	 * placed over them (i.e., setBlock() method of Cell). The move history should
	 * be initialized as empty.
	 * 
	 * @param grid   a 2D array of cells which is expected to be a rectangular shape
	 * @param blocks list of blocks already containing row-column position which
	 *               should be placed on the board
	 */
	public Board(Cell[][] grid, ArrayList<Block> blocks)
	{
		this.blocks = blocks;
		this.grid = grid;
		isGameEnded = false;
		moveHistory = new ArrayList<Move>();
		grabbedBlock = null;
		grabbedCell = null;
		allPossibleMoves = new ArrayList<Move>();
		totMoves = 0;

		int k = 0;
		for (int i = 0; i <= grid.length - 1; i++)
		{
			for (int j = 0; j <= grid[i].length - 1; j++)
			{
				if (blocks.get(k).getFirstCol() == j && blocks.get(k).getFirstRow() == i)
				{
					if (blocks.get(k).getOrientation() == HORIZONTAL)
					{
						int currentLength = blocks.get(k).getLength();
						while (!(currentLength < 1))
						{
							grid[i][j + currentLength - 1].setBlock(blocks.get(k));
							currentLength--;
						}
					}

					else if (blocks.get(k).getOrientation() == VERTICAL)
					{
						int currentLength = blocks.get(k).getLength();
						while (!(currentLength < 1))
						{
							grid[i + currentLength - 1][j].setBlock(blocks.get(k));
							currentLength--;
						}
					}

					if (k < blocks.size() - 1)
					{
						k++;
					}

				}
			}
		}
	}

	/**
	 * Constructs a new board from a given 2D array of String descriptions.
	 * <p>
	 * DO NOT MODIFY THIS CONSTRUCTOR
	 * 
	 * @param desc 2D array of descriptions
	 */
	public Board(String[][] desc)
	{
		this(GridUtil.createGrid(desc), GridUtil.findBlocks(desc));
	}

	/**
	 * Models the user grabbing a block over the given row and column. The purpose
	 * of grabbing a block is for the user to be able to drag the block to a new
	 * position, which is performed by calling moveGrabbedBlock(). This method
	 * records two things: the block that has been grabbed and the cell at which it
	 * was grabbed.
	 * 
	 * @param row row to grab the block from
	 * @param col column to grab the block from
	 */
	public void grabBlockAtCell(int row, int col)
	{
		if (grid[row][col].hasBlock())
		{
			grabbedBlock = grid[row][col].getBlock();
			grabbedCell = grid[row][col];
		}
	}

	/**
	 * Set the currently grabbed block to null.
	 */
	public void releaseBlock()
	{
		grabbedBlock = null;
	}

	/**
	 * Returns the currently grabbed block.
	 * 
	 * @return the current block
	 */
	public Block getGrabbedBlock()
	{
		return grabbedBlock;
	}

	/**
	 * Returns the currently grabbed cell.
	 * 
	 * @return the current cell
	 */
	public Cell getGrabbedCell()
	{
		return grabbedCell;
	}

	/**
	 * Returns true if the cell at the given row and column is available for a block
	 * to be placed over it. Blocks can only be placed over floors and exits. A
	 * block cannot be placed over a cell that is occupied by another block.
	 * 
	 * @param row row location of the cell.
	 * @param col column location of the cell.
	 * @return true if the cell is available for a block, otherwise false.
	 */
	public boolean canPlaceBlock(int row, int col)

	{
		if (!isGameOver())
		{
			// checking for walls and blocks
			if (grid[row][col].isWall() || grid[row][col].hasBlock()) //row >= grid.length || col >= grid[0].length || col <= 0 || row <= 0
			{
				return false;
			}

			// if its an exit or wall or theres no blocks, return true
			else if ((grid[row][col].isFloor() || grid[row][col].isExit()) && !grid[row][col].hasBlock())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the number of moves made so far in the game.
	 * 
	 * @return the number of moves
	 */
	public int getMoveCount()
	{
		return moveHistory.size();
	}

	/**
	 * Returns the number of rows of the board.
	 * 
	 * @return number of rows
	 */
	public int getRowSize()
	{
		return grid.length;
	}

	/**
	 * Returns the number of columns of the board.
	 * 
	 * @return number of columns
	 */
	public int getColSize()
	{
		return grid[0].length;
	}

	/**
	 * Returns the cell located at a given row and column.
	 * 
	 * @param row the given row
	 * @param col the given column
	 * @return the cell at the specified location
	 */
	public Cell getCell(int row, int col)
	{
		return grid[row][col];
	}

	/**
	 * Returns a list of all blocks on the board.
	 * 
	 * @return a list of all blocks
	 */
	public ArrayList<Block> getBlocks()
	{
		return blocks;
	}

	/**
	 * Returns true if the player has completed the puzzle by positioning a block
	 * over an exit, false otherwise.
	 * 
	 * @return true if the game is over
	 */
	public boolean isGameOver()
	{
		int exitCol = 0;
		int exitRow = 0;

		// get the exit row and column.
		for (int i = 0; i < grid.length; i++)
		{
			for (int j = 0; j < grid[0].length; j++)
			{
				if (grid[i][j].isExit())
				{
					exitRow = i;
					exitCol = j;
				}
			}
		}

		// if the end of the block is coinciding with the start of the exit game over is true.
		for (int k = 0; k < blocks.size(); k++)
		{
			Block block = blocks.get(k);

			if (block.getOrientation() == HORIZONTAL)
			{
				if (block.getFirstRow() == exitRow && block.getFirstCol() + block.getLength() - 1 == exitCol)
				{
					isGameEnded = true;
				}
			}

			else
			{
				if (block.getFirstRow() + block.getLength() - 1 == exitRow && block.getFirstCol() == exitCol)
				{
					isGameEnded = true;
				}
			}
		}

		return isGameEnded;
	}

	/**
	 * Moves the currently grabbed block by one cell in the given direction. A
	 * horizontal block is only allowed to move right and left and a vertical block
	 * is only allowed to move up and down. A block can only move over a cell that
	 * is a floor or exit and is not already occupied by another block. The method
	 * does nothing under any of the following conditions:
	 * <ul>
	 * <li>The game is over.</li>
	 * <li>No block is currently grabbed by the user.</li>
	 * <li>A block is currently grabbed by the user, but the block is not allowed to
	 * move in the given direction.</li>
	 * </ul>
	 * If none of the above conditions are meet, the method does the following:
	 * <ul>
	 * <li>Moves the block object by calling its move method.</li>
	 * <li>Sets the block for the grid cell that the block is being moved into.</li>
	 * <li>For the grid cell that the block is being moved out of, sets the block to
	 * null.</li>
	 * <li>Moves the currently grabbed cell by one cell in the same moved direction.
	 * The purpose of this is to make the currently grabbed cell move with the block
	 * as it is being dragged by the user.</li>
	 * <li>Adds the move to the end of the moveHistory list.</li>
	 * <li>Increment the count of total moves made in the game.</li>
	 * </ul>
	 * 
	 * @param dir the direction to move
	 */
	public void moveGrabbedBlock(Direction dir)
	{
		if (!isGameOver())
		{
			if (!(grabbedBlock == null))
			{
				int length = grabbedBlock.getLength();

				if (grabbedBlock.getOrientation() == HORIZONTAL)
				{
					if (!(dir == UP || dir == DOWN))
					{
						if (dir == RIGHT)
						{
							if (canPlaceBlock(grabbedBlock.getFirstRow(), grabbedBlock.getFirstCol() + length))
							{
								// add the move to moveHistory.
								Move moveDone = new Move(grabbedBlock, dir);
								moveHistory.add(moveDone);

								// call its respective move method.
								grabbedBlock.move(RIGHT);

								//update the grabbed cell.
								grabbedCell = grid[grabbedCell.getRow()][grabbedCell.getCol() + 1];

								//move the entire block corresponding to direction
								grid[grabbedBlock.getFirstRow()][grabbedBlock.getFirstCol() + length - 1].setBlock(grabbedBlock);

								// now set the initial positions occupied by it to null.
								grid[grabbedBlock.getFirstRow()][grabbedBlock.getFirstCol() - 1].clearBlock();

								//check whether it reached the exit and ended the game.
								isGameOver();
								
								totMoves++;
							}

						}

						else if (dir == LEFT)
						{
							if (canPlaceBlock(grabbedBlock.getFirstRow(), grabbedBlock.getFirstCol() - 1))
							{
								// add the move to moveHistory.
								Move moveDone = new Move(grabbedBlock, dir);
								moveHistory.add(moveDone);

								// call its respective move method.
								grabbedBlock.move(LEFT);

								//update the grabbed cell.
								grabbedCell = grid[grabbedCell.getRow()][grabbedCell.getCol() - 1];

								//move the entire block corresponding to direction
								grid[grabbedBlock.getFirstRow()][grabbedBlock.getFirstCol()].setBlock(grabbedBlock);

								// now set the initial positions occupied by it to null.
								grid[grabbedBlock.getFirstRow()][grabbedBlock.getFirstCol() + grabbedBlock.getLength()].clearBlock();

								//check whether it reached the exit and ended the game.
								isGameOver();
								
								totMoves++;
							}
						}
					}
				}

				else if (grabbedBlock.getOrientation() == VERTICAL)
				{
					if (!(dir == RIGHT || dir == LEFT))
					{
						if (dir == UP)
						{
							if (canPlaceBlock(grabbedBlock.getFirstRow() - 1, grabbedBlock.getFirstCol()))
							{
								// add the move to moveHistory.
								Move moveDone = new Move(grabbedBlock, dir);
								moveHistory.add(moveDone);

								// call its respective move method.
								grabbedBlock.move(UP);

								//update the grabbed cell.
								grabbedCell = grid[grabbedCell.getRow() - 1][grabbedCell.getCol()];

								//move the entire block corresponding to direction
								grid[grabbedBlock.getFirstRow()][grabbedBlock.getFirstCol()].setBlock(grabbedBlock);

								// now set the initial positions occupied by it to null.
								grid[grabbedBlock.getFirstRow() + grabbedBlock.getLength()][grabbedBlock.getFirstCol()].clearBlock();

								//check whether it reached the exit and ended the game.
								isGameOver();
								
								totMoves++;
							}
						}

						else if (dir == DOWN)
						{
							if (canPlaceBlock(grabbedBlock.getFirstRow() + length, grabbedBlock.getFirstCol()))
							{
								// add the move to moveHistory.
								Move moveDone = new Move(grabbedBlock, dir);
								moveHistory.add(moveDone);

								// call its respective move method.
								grabbedBlock.move(DOWN);

								//update the grabbed cell.
								grabbedCell = grid[grabbedCell.getRow() + 1][grabbedCell.getCol()];

								//move the entire block corresponding to direction
								grid[grabbedBlock.getFirstRow() + grabbedBlock.getLength() - 1][grabbedBlock.getFirstCol()].setBlock(grabbedBlock);

								// now set the initial positions occupied by it to null.
								grid[grabbedBlock.getFirstRow() - 1][grabbedBlock.getFirstCol()].clearBlock();

								//check whether it reached the exit and ended the game.
								isGameOver();
								
								totMoves++;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Resets the state of the game back to the start, which includes the move
	 * count, the move history, and whether the game is over. The method calls the
	 * reset method of each block object. It also updates each grid cells by calling
	 * their setBlock method to either set a block if one is located over the cell
	 * or set null if no block is located over the cell.
	 */
	public void reset()
	{

		// find each block and reset it
		for (int i = 0; i < grid.length; i++)
		{
			for (int j = 0; j < grid[i].length; j++)
			{
				if (grid[i][j].hasBlock())
				{
					grid[i][j].getBlock().reset();
				}
			}
		}

		// set each block to null (because we're calling reset after moving the blocks.)
		for (int i = 0; i < grid.length; i++)
		{
			for (int j = 0; j < grid[i].length; j++)
			{
				if (grid[i][j].hasBlock())
				{
					grid[i][j].setBlock(null);
				}
			}
		}

		// reconstruct the initial blocks onto the grid.
		int k = 0;
		for (int i = 0; i <= grid.length - 1; i++)
		{
			for (int j = 0; j <= grid[i].length - 1; j++)
			{
				if (blocks.get(k).getFirstCol() == j && blocks.get(k).getFirstRow() == i)
				{
					if (blocks.get(k).getOrientation() == HORIZONTAL)
					{
						int currentLength = blocks.get(k).getLength();
						while (!(currentLength < 1))
						{
							grid[i][j + currentLength - 1].setBlock(blocks.get(k));
							currentLength--;
						}
					}

					else if (blocks.get(k).getOrientation() == VERTICAL)
					{
						int currentLength = blocks.get(k).getLength();
						while (!(currentLength < 1))
						{
							grid[i + currentLength - 1][j].setBlock(blocks.get(k));
							currentLength--;
						}
					}

					if (k < blocks.size() - 1)
					{
						k++;
					}

				}
			}
		}
		
		totMoves = 0;

		moveHistory.clear();

		isGameEnded = false;
	}

	/**
	 * Returns a list of all legal moves that can be made by any block on the
	 * current board. If the game is over there are no legal moves.
	 * 
	 * @return a list of legal moves
	 */
	public ArrayList<Move> getAllPossibleMoves()
	{
		if (!isGameOver())
		{
			for (int i = 0; i < blocks.size(); i++)
			{
				Block moveBlock = blocks.get(i);

				if (moveBlock.getOrientation() == HORIZONTAL)
				{
					if (canPlaceBlock(moveBlock.getFirstRow(), moveBlock.getFirstCol() + moveBlock.getLength()))
					{
						if (!isGameOver())
						{
							Move possibleRight = new Move(moveBlock, RIGHT);
							allPossibleMoves.add(possibleRight);
						}
					}

					if (canPlaceBlock(moveBlock.getFirstRow(), moveBlock.getFirstCol() - 1))
					{
						if (!isGameOver())
						{
							Move possibleLeft = new Move(moveBlock, LEFT);
							allPossibleMoves.add(possibleLeft);
						}
					}

				}

				else if (moveBlock.getOrientation() == VERTICAL)
				{
					if (canPlaceBlock(moveBlock.getFirstRow() - 1, moveBlock.getFirstCol()))
					{
						if (!isGameOver())
						{
							Move possibleUp = new Move(moveBlock, UP);
							allPossibleMoves.add(possibleUp);
						}

					}

					if (canPlaceBlock(moveBlock.getFirstRow() + moveBlock.getLength(), moveBlock.getFirstCol()))
					{
						if (!isGameOver())
						{
							Move possibleDown = new Move(moveBlock, DOWN);
							allPossibleMoves.add(possibleDown);
						}
					}
				}
			}
		}

		return allPossibleMoves;
	}

	/**
	 * Gets the list of all moves performed to get to the current position on the
	 * board.
	 * 
	 * @return a list of moves performed to get to the current position
	 */
	public ArrayList<Move> getMoveHistory()
	{
		return moveHistory;
	}

	/**
	 * EXTRA CREDIT 5 POINTS
	 * <p>
	 * This method is only used by the Solver.
	 * <p>
	 * Undo the previous move. The method gets the last move on the moveHistory list
	 * and performs the opposite actions of that move, which are the following:
	 * <ul>
	 * <li>grabs the moved block and calls moveGrabbedBlock passing the opposite
	 * direction</li>
	 * <li>decreases the total move count by two to undo the effect of calling
	 * moveGrabbedBlock twice</li>
	 * <li>if required, sets is game over to false</li>
	 * <li>removes the move from the moveHistory list</li>
	 * </ul>
	 * If the moveHistory list is empty this method does nothing.
	 */
	public void undoMove()
	{

		if (isGameOver())
		{
			isGameEnded = false;
		}

		if (moveHistory.size() > 0)
		{
			if (moveHistory.size() == 1)
			{
				reset();
			}

			else
			{
				if (grabbedBlock.getOrientation() == HORIZONTAL)
				{
					Move earlierMove = moveHistory.get(moveHistory.size() - 1);

					if (earlierMove.getDirection() == RIGHT)
					{
						moveGrabbedBlock(LEFT);
						moveHistory.remove(moveHistory.size() - 1);
						moveHistory.remove(moveHistory.size() - 2);
						totMoves-=2;
					}

					else if (earlierMove.getDirection() == LEFT)
					{
						moveGrabbedBlock(RIGHT);
						moveHistory.remove(moveHistory.size() - 1);
						moveHistory.remove(moveHistory.size() - 2);
						totMoves-=2;
					}
				}

				else if (grabbedBlock.getOrientation() == VERTICAL)
				{
					Move earlierMove = moveHistory.get(moveHistory.size()-1);

					if (earlierMove.getDirection() == UP)
					{
						moveGrabbedBlock(DOWN);
						moveHistory.remove(moveHistory.size() - 1);
						moveHistory.remove(moveHistory.size() - 2);
						totMoves-=2;
					}

					else if (earlierMove.getDirection() == DOWN)
					{
						moveGrabbedBlock(UP);
						moveHistory.remove(moveHistory.size() - 1);
						moveHistory.remove(moveHistory.size() - 2);
						totMoves-=2;
					}
				}
			}
		}
	}

	@Override
	public String toString()
	{
		StringBuffer buff = new StringBuffer();
		boolean first = true;
		for (Cell row[] : grid)
		{
			if (!first)
			{
				buff.append("\n");
			} else
			{
				first = false;
			}
			for (Cell cell : row)
			{
				buff.append(cell.toString());
				buff.append(" ");
			}
		}
		return buff.toString();
	}
}
