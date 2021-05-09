/**
 * Write a description of class GameOfNine here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class GameOfNine extends JPanel
{
    private int size; //size of game
    private int nbTiles; // number of tiles
    private int dimension;// grid UI dimesion
    private static final Color FOREGROUND_COLOUR = new Color(200,43,80); //colour
    private static final Random RANDOM = new Random(); // Onject to shuffle tiles

    private int[] tiles; // stores tiles in array of integers 
    private int tileSize;//size of tile 
    private int blankPosition;// position of blank tile
    private int margin;//margin of the grid on the frame

    private JButton shuffle;
    
    private int gridSize;//grid size
    private boolean gameOver;// true if game is over, false if game continues

    public GameOfNine(int size, int dime, int mar)
    {
        this.size = size;
        dimension = dime;
        margin = mar;

        nbTiles = size  - 1;
        tiles = new int[size * size];
        
        // nbTiles = size * size - 1;
        // tiles = new int[size * size];

        gridSize = (dime - 2 * margin);
        tileSize = gridSize / size;

        setPreferredSize(new Dimension(dimension, dimension + margin));
        setBackground(Color.WHITE);
        setForeground(FOREGROUND_COLOUR);
        setFont(new Font("SansSerif", Font.BOLD,60));
        
        shuffle = new JButton("Shuffle");
        shuffle.setSize(200,60);
        shuffle.setLocation(550,430);
        shuffle.setFont(new Font("Serif",Font.BOLD,26));
        add(shuffle);
        
        ButtonHandler handler = new ButtonHandler();
        shuffle.addActionListener(handler);

        gameOver = true;

        addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mousePressed(MouseEvent e) //For interaction with grid by clicks
            {
                if(gameOver)
                {
                    newGame();
                }
                else
                {
                    int ex = e.getX() - margin;
                    int ey = e.getY() - margin;
                    
                    if(ex < 0 || ex > gridSize || ey < 0 || ey > gridSize)
                    {
                        return;
                    }
                    
                    //position in grid
                    int c1 = ex/tileSize;
                    int r1 =  ey/tileSize;
                    
                    //pos of blank cell
                    int c2 = blankPosition % size;
                    int r2 = blankPosition / size;
                    
                    int clickPosition = r1 * size + c1;
                    //Direction
                    int dir = 0;
                    if(c1 == c2 && Math.abs(r1 - r2) > 0)
                    {
                        dir = (r1 - r2) > 0 ? size : -size;
                    }
                    else if(r1 == r2 && Math.abs(c1 - c2) > 0)
                    {
                        dir = (c1 - c2) > 0 ? 1 : -1;
                    }
                    if(dir != 0)
                    {
                        //Move Tiles In Direction
                        do
                        {
                            int newBlankPosition = blankPosition + dir;
                            tiles[blankPosition] = tiles[newBlankPosition];
                            blankPosition = newBlankPosition;
                        }while(blankPosition != clickPosition);
                        
                        tiles[blankPosition] = 0;
                    }
                    
                    gameOver = isSolved();//check if game is solved
                }
                
                repaint();//repaint Panel
            }
        });
        
        newGame();
    }
    
    private void newGame()
    {
        do
        {
            reset(); //reset to initial state
            shuffle(); // shuffle tiles
        }while(!isSolvable());
        
        gameOver = false;
    }
    
    private void reset()
    {
        for (int i = 0; i < tiles.length; i++)
        {
            tiles[i] = (i+1) % tiles.length;
        }
        
        blankPosition = tiles.length - 1;  // Blank square position
    }
    
    private void shuffle()
    {
        int n = nbTiles;
        while (n > 1)
        {
            int rand = RANDOM.nextInt(n--);
            int temporary = tiles[rand];
            tiles[rand] = tiles[n];
            tiles[n] = temporary;
        }
    }
    
    public boolean isSolvable()
    {
        int countTransposition = 0;
        
        for(int i = 0; i < nbTiles; i++)
        {
            for(int j = 0; j < i; j++)
            {
                if(tiles[j] >  tiles[i])
                {
                    countTransposition++;
                }
            }
        }
        
        return countTransposition % 2 == 0;
    }
    
    private boolean isSolved()
    {
        if(tiles[tiles.length - 1] != 0)
        {
            return false;
        }
        
        for(int i = nbTiles - 1; i >= 0; i--)
        {
            if(tiles[i] != i + 1)
            {
                return false;
            }
        }
        
        return true;
    }
    
    private void drawGrid(Graphics2D g)
    {
        for(int i = 0; i < tiles.length; i++)
        {
            int r = i / size;
            int c = i % size;
            
            int x = margin + c * tileSize;
            int y = margin + r * tileSize;
            
            if(tiles[i] == 0)//Special case for the blank tile
            {
                if(gameOver)
                {
                    g.setColor(FOREGROUND_COLOUR);
                    drawCentredString(g, "Done", x,y);
                    
                }
                continue;
            }
            g.setColor(getForeground());
            g.fillRoundRect(x,y,tileSize,tileSize,25,25);
            g.setColor(Color.BLACK);
            g.drawRoundRect(x,y,tileSize,tileSize,25,25);
            g.setColor(Color.WHITE);
            
            drawCentredString(g, String.valueOf(tiles[i]) ,x ,y);
        }
    }
    
    private void drawStartMessage(Graphics2D g)
    {
        if(gameOver)
        {
            g.setFont(getFont().deriveFont(Font.BOLD, 18));
            g.setColor(FOREGROUND_COLOUR);
            String s = "Click to Start a New Game";
            g.drawString(s,(getWidth() - g.getFontMetrics().stringWidth(s))/2,getHeight() - margin);
        }
    }
    
    private void drawCentredString(Graphics2D g, String s, int x, int y)
    {
        FontMetrics fm = g.getFontMetrics();
        int asc = fm.getAscent();
        int desc = fm.getDescent();
        g.drawString(s, x + (tileSize - fm.stringWidth(s))/2 ,y + (asc + (tileSize - (asc + desc))/2));
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D)g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawGrid(graphics2D);
        drawStartMessage(graphics2D);
    }
    
    private class ButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() == shuffle)
            {
                shuffle();
            }
            
        }

    }
    
    public static void main(String [] args)
    {
        SwingUtilities.invokeLater(() -> 
        {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("9 Way Slide Puzzle");
            frame.setResizable(false);
            frame.add(new GameOfNine(3,600,50), BorderLayout.CENTER);
            frame.pack();
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
