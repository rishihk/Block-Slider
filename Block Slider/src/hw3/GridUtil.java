package hw3;

import static api.Orientation.*;
import static api.CellType.*;

import java.util.ArrayList;

import api.Cell;

/**
 * Utilities for parsing string descriptions of a grid.
 */
public class GridUtil
{
	/**
	 * Constructs a 2D grid of Cell objects given a 2D array of cell descriptions.
	 * String descriptions are a single character and have the following meaning.
	 * <ul>
	 * <li>"*" represents a wall.</li>
	 * <li>"e" represents an exit.</li>
	 * <li>"." represents a floor.</li>
	 * <li>"[", "]", "^", "v", or "#" represent a part of a block. A block is not a
	 * type of cell, it is something placed on a cell floor. For these descriptions
	 * a cell is created with CellType of FLOOR. This method does not create any
	 * blocks or set blocks on cells.</li>
	 * </ul>
	 * The method only creates cells and not blocks. See the other utility method
	 * findBlocks which is used to create the blocks.
	 * 
	 * @param desc a 2D array of strings describing the grid
	 * @return a 2D array of cells the represent the grid without any blocks present
	 */
	public static Cell[][] createGrid(String[][] desc)
	{
		int rows = desc.length;
		int cols = desc[0].length;

		Cell[][] cellObj = new Cell[rows][cols];

		for (int i = 0; i < desc.length; i++)
		{
			for (int j = 0; j < desc[i].length; j++)
			{

				if (desc[i][j].equals("*"))
				{
					Cell wallCell = new Cell(WALL, i, j);
					cellObj[i][j] = wallCell;
				}

				else if (desc[i][j].equals("e"))
				{
					Cell exitCell = new Cell(EXIT, i, j);
					cellObj[i][j] = exitCell;
				}

				else
				{
					Cell floorCell = new Cell(FLOOR, i, j);
					cellObj[i][j] = floorCell;
				}

			}
		}

		return cellObj;
	}

	/**
	 * Returns a list of blocks that are constructed from a given 2D array of cell
	 * descriptions. String descriptions are a single character and have the
	 * following meanings.
	 * <ul>
	 * <li>"[" the start (left most column) of a horizontal block</li>
	 * <li>"]" the end (right most column) of a horizontal block</li>
	 * <li>"^" the start (top most row) of a vertical block</li>
	 * <li>"v" the end (bottom most column) of a vertical block</li>
	 * <li>"#" inner segments of a block, these are always placed between the start
	 * and end of the block</li>
	 * <li>"*", ".", and "e" symbols that describe cell types, meaning there is not
	 * block currently over the cell</li>
	 * </ul>
	 * 
	 * @param desc a 2D array of strings describing the grid
	 * @return a list of blocks found in the given grid description
	 */
	public static ArrayList<Block> findBlocks(String[][] desc)
	{
		ArrayList<Block> blockObj = new ArrayList<Block>();
		int horLength = 2;
		int verLength = 2;

		int moveHorLy = 1;
		int moveVerLy = 1;

		for (int i = 0; i < desc.length; i++)
		{
			for (int j = 0; j < desc[i].length; j++)
			{
				if (desc[i][j].equals("["))
				{
					while (desc[i][j + moveHorLy].equals("#"))
					{
						horLength++;
						moveHorLy++;
					}

					Block horBlock = new Block(i, j, horLength, HORIZONTAL);
					blockObj.add(horBlock);

					horLength = 2;
					moveHorLy = 1;

				}

				else if (desc[i][j].equals("^"))
				{

					while (desc[i + moveVerLy][j].equals("#"))
					{
						verLength++;
						moveVerLy++;
					}

					Block verBlock = new Block(i, j, verLength, VERTICAL);
					blockObj.add(verBlock);

					verLength = 2;
					moveVerLy = 1;

				}

			}

		}

		return blockObj;
	}
}
