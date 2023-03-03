import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Map {
    // マップ
    private char[][] map;

    // タイルサイズ
    private static final int TILE_SIZE = 30;
    // 行数
    private static int ROW;
    // 列数
    private static int COL;
    // 幅
    public static int WIDTH;
    // 高さ
    public static int HEIGHT;
    // ブロックが壊れる音
    private AudioClip breakSound;
    // はてなブロックをたたいた音
    private AudioClip hatenaSound;
    // ヒントブロックをたたいた音
    private AudioClip hintoSound;
    // ヒントブロックをたたいたか
    private boolean isHinto;


    // ブロックなど画像
    private Image blockImage;
    private Image hatenaImage;
    private Image block2Image;
    private Image dokanImage;
    private Image goalImage;
    private Image chairImage;
    private Image hintoImage;
    private Image sakeImage;
    private Image kinokoImage;

    public Map(String filename) {
      load(filename);

      WIDTH = TILE_SIZE * COL;
      HEIGHT = TILE_SIZE * ROW;
      // イメージをロードする
      loadImage();
      // サウンドをロードする
      breakSound = Applet.newAudioClip(getClass().getResource("SE/blockbreak.wav"));
      hatenaSound = Applet.newAudioClip(getClass().getResource("SE/hatena.wav"));
      hintoSound = Applet.newAudioClip(getClass().getResource("SE/hinto.wav"));
      isHinto = false;
    }

    /**
     * マップを描画する
     *
     */
    public void draw(Graphics g, int offsetX, int offsetY) {

        // オフセットを元に描画範囲を求める
        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX + pixelsToTiles(MainPanel.WIDTH) + 1;
        // 描画範囲がマップの大きさより大きくならないように調整
        lastTileX = Math.min(lastTileX, COL);

        int firstTileY = pixelsToTiles(-offsetY);
        int lastTileY = firstTileY + pixelsToTiles(MainPanel.HEIGHT) + 1;
        // 描画範囲がマップの大きさより大きくならないように調整
        lastTileY = Math.min(lastTileY, ROW);

        for (int i = firstTileY; i < lastTileY; i++) {
          for (int j = firstTileX; j < lastTileX; j++) {
              // mapの値に応じて画像を描く
              switch (map[i][j]) {
                  case 'B' : // レンガブロック
                      g.drawImage(blockImage, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY, null);
                      break;
                  case 'C' : // はてなブロック
                      g.drawImage(hatenaImage, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY, null);
                      break;
                  case 'D' : // ただのブロック
                      g.drawImage(block2Image, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY, null);
                      break;
                  case 'E' : // 隠しブロック
                      break;
                  case 'F' : // 隠しブロックのセリフ
                      g.setColor(Color.BLACK);
                      g.drawString("俺が見えるのか？？", tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY);
                      break;
                  case 'G' : // 土管を描画
                      g.drawImage(dokanImage, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY, null);
                      break;
                  case 'I' : // ゴールを描画
                      g.drawImage(goalImage, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY, null);
                      break;
                  case 'K' : // 椅子を描画
                      g.drawImage(chairImage, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY, null);
                      break;
                  case 'A' : // ヒントブロック
                      g.drawImage(hintoImage, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY, null);
                      break;
                  case 'L' : // ヒント
                      g.setColor(Color.BLACK);
                      g.fillRect(100,100,500,300);
                      g.setColor(Color.WHITE);
                      g.drawString("ジャンプについて",150,150);
                      g.drawString("上キーを押すとジャンプ！！",150,200);
                      g.drawString("ジャンプは上キーを長く押すほど高くジャンプできます",150,250);
                      g.drawString("(もう一度ヒントブロックをたたいて削除)",200,350);
                      isHinto = true;
                      break;
                  case 'M' : // アイテム(酒)
                      g.drawImage(sakeImage, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY, null);
                      break;
                  case 'O' : // 第二のアイテムブロック
                      g.drawImage(hatenaImage, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY, null);
                      break;
                  case 'P' : // アイテム(きのこ)
                      g.drawImage(kinokoImage, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY, null);
                      break;
                }
            }
        }
    }

    /**
     * (newX, newY)で衝突するブロックの座標を返す
     * @return 衝突するブロックの座標
     */
    public Point getTileCollision(Player player, double newX, double newY) {
        // 小数点以下切り上げ
        // 浮動小数点の関係で切り上げしないと衝突してないと判定される場合がある
        newX = Math.ceil(newX);
        newY = Math.ceil(newY);

        double fromX = Math.min(player.getX(), newX);
        double fromY = Math.min(player.getY(), newY);
        double toX = Math.max(player.getX(), newX);
        double toY = Math.max(player.getY(), newY);

        int fromTileX = pixelsToTiles(fromX);
        int fromTileY = pixelsToTiles(fromY);
        int toTileX = pixelsToTiles(toX + Player.WIDTH - 1);
        int toTileY = pixelsToTiles(toY + Player.HEIGHT - 1);

        // 衝突しているか調べる
        for (int x = fromTileX; x <= toTileX; x++) {
            for (int y = fromTileY; y <= toTileY; y++) {
                // 画面外は衝突
                if (x < 0 || x >= COL) {
                    return new Point(x, y);
                }
                if (y < 0 || y >= ROW) {
                    return new Point(x, y);
                }
                // ブロックがあったら衝突
                if (map[y][x] == 'B') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'C') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'D') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'E') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'H') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'G') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'J') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'A') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'M') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'O') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'P') {
                    return new Point(x, y);
                }
            }
        }

        return null;
    }


    public Point getTileCollision2(Enemy enemy, double newX, double newY) {
        // 小数点以下切り上げ
        // 浮動小数点の関係で切り上げしないと衝突してないと判定される場合がある
        newX = Math.ceil(newX);
        newY = Math.ceil(newY);

        double fromX = Math.min(enemy.getX(), newX);
        double fromY = Math.min(enemy.getY(), newY);
        double toX = Math.max(enemy.getX(), newX);
        double toY = Math.max(enemy.getY(), newY);

        int fromTileX = pixelsToTiles(fromX);
        int fromTileY = pixelsToTiles(fromY);
        int toTileX = pixelsToTiles(toX + Enemy.WIDTH - 1);
        int toTileY = pixelsToTiles(toY + Enemy.HEIGHT - 1);

        // 衝突しているか調べる
        for (int x = fromTileX; x <= toTileX; x++) {
            for (int y = fromTileY; y <= toTileY; y++) {
                // 画面外は衝突
                if (x < 0 || x >= COL) {
                    return new Point(x, y);
                }
                if (y < 0 || y >= ROW) {
                    return new Point(x, y);
                }
                // ブロックがあったら衝突
                if (map[y][x] == 'B') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'C') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'D') {
                    return new Point(x, y);
                }
            }
        }

        return null;
    }public Point getTileCollision3(Daigo daigo, double newX, double newY) {
        // 小数点以下切り上げ
        // 浮動小数点の関係で切り上げしないと衝突してないと判定される場合がある
        newX = Math.ceil(newX);
        newY = Math.ceil(newY);

        double fromX = Math.min(daigo.getX(), newX);
        double fromY = Math.min(daigo.getY(), newY);
        double toX = Math.max(daigo.getX(), newX);
        double toY = Math.max(daigo.getY(), newY);

        int fromTileX = pixelsToTiles(fromX);
        int fromTileY = pixelsToTiles(fromY);
        int toTileX = pixelsToTiles(toX + daigo.WIDTH - 1);
        int toTileY = pixelsToTiles(toY + daigo.HEIGHT - 1);

        // 衝突しているか調べる
        for (int x = fromTileX; x <= toTileX; x++) {
            for (int y = fromTileY; y <= toTileY; y++) {
                // 画面外は衝突
                if (x < 0 || x >= COL) {
                    return new Point(x, y);
                }
                if (y < 0 || y >= ROW) {
                    return new Point(x, y);
                }
                // ブロックがあったら衝突
                if (map[y][x] == 'B') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'C') {
                    return new Point(x, y);
                }
                if (map[y][x] == 'D') {
                    return new Point(x, y);
                }
            }
        }

        return null;
      }
    /**
    * レンガブロックがあるか
    * @return レンガブロックがあったらtrue
    */
   public boolean isBrickBlock(int x, int y) {
       if (map[y][x] == 'B') {
           return true;
       }

       return false;
   }
   /**
    * レンガブロックを壊した状態にする
    */
   public void BreakBrickBlock(int x, int y) {
       // 叩かれた後のブロックに変化
       breakSound.play();
       map[y][x] = 0;
   }
   /**
    * ヒントを表示
    */
   public void Hinto(int x, int y) {
        if(isHinto){
         map[y-1][x] = 'N'; // ヒントを消す
         isHinto = false;
       }else{
         map[y-1][x] = 'L'; // ヒントを表示する
         hintoSound.play();
       }


   }
   /**
   * はてなブロックがあるか
   * @return はてなブロックがあったらtrue
   */
  public boolean isHatenaBlock(int x, int y) {
      if (map[y][x] == 'C') {
          return true;
      }
      return false;
    }
    /**
    * 第二のアイテムブロックブロックがあるか
    * @return あったらtrue
    */
   public boolean isHatena2Block(int x, int y) {
       if (map[y][x] == 'O') {
           return true;
       }
       return false;
     }
    /**
    * 隠しブロックがあるか
    * @return ブロックがあったらtrue
    */
   public boolean isHideBlock(int x, int y) {
       if (map[y][x] == 'E') {
           return true;
       }
       return false;
     }
     /**
     * ヒントブロックがあるか
     * @return ブロックがあったらtrue
     */
    public boolean isHintoBlock(int x, int y) {
        if (map[y][x] == 'A') {
            return true;
        }
        return false;
      }
     /**
     * 土管があるか
     * @return 土管があったらtrue
     */
    public boolean isDokan(int x, int y) {
        if (map[y][x] == 'G' || map[y][x] == 'H') {
            return true;
        }
        return false;
      }
      /**
      * ゴールしたか
      * @return ゴールしたらtrue
      */
     public boolean isGoal(int x, int y) {
         if (map[y][x] == 'J') {
             return true;
         }
         return false;
       }
     /**
     * 隠しブロックの横に文字を書く
     */
    public void HideString(int x, int y) {
        map[y][x] = 'F';
      }

    /**
     * はてなブロックを硬いブロックの状態にする
     */
    public void changeBlock(int x, int y) {
        // 叩かれた後のブロックに変化
        map[y][x] = 'D';
        hatenaSound.play();
    }
    /**
     * アイテム(酒)を表示する
     */
    public void sake(int x, int y) {
        map[y-1][x] = 'M';

    }
    /**
     * アイテム(キノコ)を表示する
     */
    public void kinoko(int x, int y) {
        map[y-1][x] = 'P';

    }
    /**
     * アイテム(酒)があるか
     * あったらtrue
     */
    public boolean isSake(int x, int y) {
        if(map[y][x] == 'M'){
          return true;
        }
        return false;

    }
    /**
     * アイテム(きのこ)があるか
     * あったらtrue
     */
    public boolean isKinoko(int x, int y) {
        if(map[y][x] == 'P'){
          return true;
        }
        return false;

    }
    /**
     * ブロックをなにもない状態にする
     */
    public void changeNull(int x, int y) {
        map[y][x] = 'N';

    }
    /**
     * 土管を描画
     */
    public void dokanDraw(Graphics g, int x, int y) {
        g.drawImage(dokanImage, x, y, null);
    }
    /**
     * ピクセル単位をタイル単位に変更する
     * @param pixels ピクセル単位
     * @return タイル単位
     */
    public static int pixelsToTiles(double pixels) {
        return (int)Math.floor(pixels / TILE_SIZE);
    }

    /**
     * タイル単位をピクセル単位に変更する
     * @param tiles タイル単位
     * @return ピクセル単位
     */
    public static int tilesToPixels(int tiles) {
        return tiles * TILE_SIZE;
    }

    /**
    * イメージをロードする
    */
    private void loadImage() {
       ImageIcon icon1 = new ImageIcon(getClass().getResource("img/block.png"));
       ImageIcon icon2 = new ImageIcon(getClass().getResource("img/hatena.png"));
       ImageIcon icon3 = new ImageIcon(getClass().getResource("img/block2.png"));
       ImageIcon icon4 = new ImageIcon(getClass().getResource("img/dokan.png"));
       ImageIcon icon5 = new ImageIcon(getClass().getResource("img/goal.png"));
       ImageIcon icon6 = new ImageIcon(getClass().getResource("img/chair.png"));
       ImageIcon icon7 = new ImageIcon(getClass().getResource("img/hinto.png"));
       ImageIcon icon8 = new ImageIcon(getClass().getResource("img/sake.png"));
       ImageIcon icon9 = new ImageIcon(getClass().getResource("img/kinoko.png"));
       blockImage = icon1.getImage();
       hatenaImage = icon2.getImage();
       block2Image = icon3.getImage();
       dokanImage = icon4.getImage();
       goalImage = icon5.getImage();
       chairImage = icon6.getImage();
       hintoImage = icon7.getImage();
       sakeImage = icon8.getImage();
       kinokoImage = icon9.getImage();
   }
   /**
     * マップをロードする
     *
     * @param map01.dat マップファイル
     */
    private void load(String filename) {
        try {
            // ファイルを開く
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("map/" + filename)));

            // 行数を読み込む
            String line = br.readLine();
            ROW = Integer.parseInt(line);
            // 列数を読み込む
            line = br.readLine();
            COL = Integer.parseInt(line);
            // マップを作成
            map = new char[ROW][COL];
            for (int i = 0; i < ROW; i++) {
                line = br.readLine();
                for (int j = 0; j < COL; j++) {
                    map[i][j] = line.charAt(j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
