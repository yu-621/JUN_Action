import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

public class Enemy {
    // 幅
    public static final int WIDTH = 38;
    // 高さ
    public static final int HEIGHT = 38;
    // スピード
    private static final double SPEED = 1;
    // 重力
    private final double GRAVITY = 3.0;

    // 位置
    protected double x;
    protected double y;
    // 速度
    protected double vx;
    protected double vy;
    //加速度
    protected double vv = 0;
    // 加加速度
    protected double vvv = -0.03;

    // マップへの参照
    protected Map map;
    // 敵の画像
    Image image;

    // 踏まれたときの音
    private AudioClip sound;

    public Enemy(double x, double y, Map map) {
        this.x = x;
        this.y = y;
        this.map = map;
        vy = 0;

        // サウンドをロード
        sound = Applet.newAudioClip(getClass().getResource("SE/humi.wav"));
        // イメージをロードする
        loadImage();
    }

    public void update() {
        // 重力で下向きに加速度がかかる
        vy += GRAVITY;
        // 超加速
        vv += vvv;
        if(vx<=0) vx += vv; // 左向きに動くときは左に超加速
        else vx -= vv; // 右向きに動くときは右に超加速

        if(x == 660) revive(WIDTH, HEIGHT);
        // x方向の当たり判定
        // 移動先座標を求める
        double newX = x + vx;
        // 移動先座標で衝突するタイルの位置を取得
        // x方向だけ考えるのでy座標は変化しないと仮定
        Point tile = map.getTileCollision2(this, newX, y);
        if (tile == null) {
            // 衝突するタイルがなければ移動
            x = newX;
        } else {
            // 衝突するタイルがある場合
            if (vx > 0) { // 右へ移動中なので右のブロックと衝突
                // ブロックにめりこむ or 隙間がないように位置調整
                x = Map.tilesToPixels(tile.x) - WIDTH;
            } else if (vx < 0) { // 左へ移動中なので左のブロックと衝突
                // 位置調整
                x = Map.tilesToPixels(tile.x + 1);
            }
            // 移動方向を反転
            vx = -vx;
        }

        // y方向の当たり判定
        // 移動先座標を求める
        double newY = y + vy;
        // 移動先座標で衝突するタイルの位置を取得
        // y方向だけ考えるのでx座標は変化しないと仮定
        tile = map.getTileCollision2(this, x, newY);
        if (tile == null) {
            // 衝突するタイルがなければ移動
            y = newY;
        } else {
            // 衝突するタイルがある場合
            if (vy > 0) { // 下へ移動中なので下のブロックと衝突（着地）
                // 位置調整
                y = Map.tilesToPixels(tile.y) - HEIGHT;
                // 着地したのでy方向速度を0に
                vy = 0;
            } else if (vy < 0) { // 上へ移動中なので上のブロックと衝突（天井ごん！）
                // 位置調整
                y = Map.tilesToPixels(tile.y + 1);
                // 天井にぶつかったのでy方向速度を0に
                vy = 0;
            }
        }
    }

    void revive(int WIDTH, int HEIGHT){
      x = 0;
      y = Map.HEIGHT - HEIGHT;

    }


    /**
    * @return Returns the x.
    */
    public double getX() {
        return x;
    }
    /**
     * @return Returns the y.
     */
    public double getY() {
        return y;
    }
    /**
     * @return Returns the width.
     */
    public int getWidth() {
        return WIDTH;
    }
    /**
     * @return Returns the height.
     */
    public int getHeight() {
        return HEIGHT;
    }


    /**
    * 敵のイメージをロードする
    */
    private void loadImage() {
      ImageIcon icon = new ImageIcon(getClass().getResource("img/nagao.png"));
      //Image image = Toolkit.getDefaultToolkit().getImage("Player.png");
      image = icon.getImage();
    }
    /**
     *敵を描画
     */
    public void draw(Graphics g, int offsetX, int offsetY) {
        g.drawImage(image, (int)x + offsetX, (int)y + offsetY, Enemy.WIDTH, Enemy.HEIGHT, null);
    }

    /**
     * サウンドを再生
     */
    public void play() {
        sound.play();
    }
}
