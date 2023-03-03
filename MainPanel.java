import java.applet.Applet;
import java.applet.AudioClip;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import java.awt.*;


public class MainPanel extends JPanel implements Runnable, KeyListener {
    Image img;
    Image img2;
    Image img3;
    Image img4;
    //Image img5;
    Image img6;
    Image img7;
    // パネルサイズ
    public static final int WIDTH = 740;
    public static final int HEIGHT = 640;

    private Player player;
    private Enemy enemy;
    private Daigo daigo;
    private int mode = -2;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;
    private static boolean downPressed;
    private boolean spacePressed;
    private boolean yetend; // クリアした時の音楽を一度だけ鳴らすための旗
    private boolean yetop;
    private boolean clearString; // ゲームクリアのコメント表示タイミングでtrue

    private Map map;
    private int count = 2;

    // サウンド
    private AudioClip opSound;
    private AudioClip deathSound;
    private AudioClip deathSound2;
    private AudioClip gamesetSound;
    private AudioClip endSound;

    // ゲームループ用スレッド
    private Thread gameLoop;

    public MainPanel() {
        // パネルの推奨サイズを設定、pack()するときに必要
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // パネルがキー入力を受け付けるようにする
        setFocusable(true);
        // キーイベントリスナーを登録
        addKeyListener(this);
        //マップの作成
        map = new Map("map01.dat");
        // プレイヤーを作成
        player = new Player(192, 524, map);
        // 敵を作成
        enemy = new Enemy(600, 542, map);
        // 敵（だいご）を作成
        daigo = new Daigo(2150, 542, map);

        img3 = Toolkit.getDefaultToolkit().getImage("img/burst.png");
        img4 = Toolkit.getDefaultToolkit().getImage("img/death3.png");
        //img5 = Toolkit.getDefaultToolkit().getImage("img/jun2.png");
        img6 = Toolkit.getDefaultToolkit().getImage("img/chair.png");
        img7 = Toolkit.getDefaultToolkit().getImage("img/jun3.png");
        // サウンド
        opSound = Applet.newAudioClip(getClass().getResource("SE/op2.wav"));
        deathSound = Applet.newAudioClip(getClass().getResource("SE/death.wav"));
        deathSound2 = Applet.newAudioClip(getClass().getResource("SE/death2.wav"));
        gamesetSound =  Applet.newAudioClip(getClass().getResource("SE/gameset.wav"));
        endSound =  Applet.newAudioClip(getClass().getResource("SE/allclear.wav"));
        yetop = true;
        yetend = true;
        clearString = false;
         // ゲームループ開始
        gameLoop = new Thread(this);
        gameLoop.start();


    }




    /**
     * ゲームループ
     */
    public void run() {
        while (true) {
            if(mode == -1){
              try{
                Thread.sleep(1800);
              } catch(InterruptedException e)
              {
                System.out.println("try-catchが必要です。");
              }
              mode = 0;
            }
            player.Goal();


            // 落ちて死んだときの処理
            if(player.Death()){
              deathSound.play();
              // 死んでから少し時間を置く
              try{
                Thread.sleep(2500);
              } catch(InterruptedException e)
              {
                System.out.println("try-catchが必要です。");
              }
              count -= 1;
              // マップを作成
              map = new Map("map01.dat");
              // プレイヤーを作成
              player = new Player(192, 520, map);
              // 敵の初期化
              enemy = new Enemy(600, 542, map);
              daigo = new Daigo(2150, 542, map);
              mode = -1;
            }
            //敵に当たった時の処理
            if(player.Death2(enemy)){
              deathSound.play();
              // 死んでから少し時間を置く
              try{
                Thread.sleep(2500);
              } catch(InterruptedException e)
              {
                System.out.println("try-catchが必要です。");
              }
              count -= 1;
              // マップを作成
              map = new Map("map01.dat");
              // プレイヤーを作成
              player = new Player(192, 520, map);
              // 敵の初期化
              enemy = new Enemy(600, 542, map);
              daigo = new Daigo(2150, 542, map);
              mode = -1;
            }
            //敵(だいご)に当たった時の処理
            if(player.Death4(daigo)){
              deathSound.play();
              // 死んでから少し時間を置く
              try{
                Thread.sleep(2500);
              } catch(InterruptedException e)
              {
                System.out.println("try-catchが必要です。");
              }
              count -= 1;
              // マップを作成
              map = new Map("map01.dat");
              // プレイヤーを作成
              player = new Player(192, 520, map);
              // 敵の初期化
              enemy = new Enemy(600, 542, map);
              daigo = new Daigo(2150, 542, map);
              mode = -1;
            }
            // 上側に行き過ぎた時の処理
            if(player.Death3()){
              gamesetSound.play();
              // 死んでから少し時間を置く
              try{
                Thread.sleep(2500);
              } catch(InterruptedException e)
              {
                System.out.println("try-catchが必要です。");
              }

              count -= 1;
              // マップを作成
              map = new Map("map01.dat");
              // プレイヤーを作成
              player = new Player(192, 520, map);
              // 敵の初期化
              enemy = new Enemy(600, 542, map);
              daigo = new Daigo(2150, 542, map);
              mode = -1;
            }
            if(player.Death5()){ // 酒で死んだときは音楽だけ違う
              deathSound2.play();
              // 死んでから少し時間を置く
              try{
                Thread.sleep(2500);
              } catch(InterruptedException e)
              {
                System.out.println("try-catchが必要です。");
              }

              count -= 1;
              // マップを作成
              map = new Map("map01.dat");
              // プレイヤーを作成
              player = new Player(192, 520, map);
              // 敵の初期化
              enemy = new Enemy(600, 542, map);
              daigo = new Daigo(2150, 542, map);
              mode = -1;
            }
            if(player.goalend()){
              try{
                Thread.sleep(4000);
              } catch(InterruptedException e)
              {
                System.out.println("try-catchが必要です。");
              }
              if(yetend){ // 音楽を一回だけ鳴らすための処理
                endSound.play();
                yetend = false;
                clearString = true;
              }

            }



            if (leftPressed) {                // 左キーが押されていれば左向きに加速
                player.accelerateLeft();
            } else if (rightPressed) {     // 右キーが押されていれば右向きに加速
                player.accelerateRight();

            } else {
              // 何も押されてないときは停止
              player.stop();
            }
            if(upPressed){
              //上キーが押されたときはジャンプ
              player.jump();
            }
            if(downPressed){
              // 下キーを押したとき
              player.indokan();
            }



            // プレイヤーの状態を更新
            player.update();
            // 敵の状態を更新
            if( mode == 0){
              enemy.update();
              daigo.update();
            }
            // 再描画
            repaint();

            // 休止
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 描画処理
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 盤面を描いたり、フィールドを描いたりする
          // イメージを読み込む
        img = Toolkit.getDefaultToolkit().getImage("img/title.png");
        img2 = Toolkit.getDefaultToolkit().getImage("img/jun.png");

        switch(mode){
        case -2: //タイトル画面
          g.drawImage(img,0,0,this);
          g.drawString("Prece      SPACE      Key", 300, 400);
          if(yetop){
            opSound.play();
            yetop = false;
          }
          break;
        case -1:
          g.setColor(Color.BLACK);
          g.fillRect(0, 0, WIDTH, HEIGHT);
          Font font1 = new Font("MS Pゴシック", Font.PLAIN,30);
          g.setColor(Color.WHITE);
          g.setFont(font1);
          g.drawString("×" + count, 450, 320);
          g.drawImage(img2,230,250,this);
          break;
        case 0:
          g.setColor(Color.cyan);
          g.fillRect(0, 0, WIDTH, HEIGHT);
          // X方向のオフセットを計算
          int offsetX = MainPanel.WIDTH / 2 - (int)player.getX();
          // マップの端ではスクロールしないようにする
          offsetX = Math.min(offsetX, 0);
          offsetX = Math.max(offsetX, MainPanel.WIDTH - Map.WIDTH);

          // Y方向のオフセットを計算
          int offsetY = MainPanel.HEIGHT / 2 - (int)player.getY();
          // マップの端ではスクロールしないようにする
          offsetY = Math.min(offsetY, 0); // 30はタイル1個分の大きさで、死んだ敵がここに移動する
          offsetY = Math.max(offsetY, MainPanel.HEIGHT - Map.HEIGHT + 30*3);
          // マップを描画
          map.draw(g, offsetX, offsetY);
          // プレイヤーを描画
          player.draw(g, offsetX, offsetY);
          // 敵を描画
          enemy.draw(g, offsetX, offsetY);
          // 敵を描画
          daigo.draw(g, offsetX, offsetY);
          // 敵で死んだ時の演出
          if(player.Death2(enemy)){
            g.setColor(Color.BLACK);
            g.drawString("俺の加速力なめんなよ！", (int)enemy.getX() + 50, 600);
          }
          // だいごは、playerとの距離が近いとplayerに合わせてジャンプする
          if(Math.abs(daigo.getX() - MainPanel.WIDTH/2 + offsetX) < 200){
            if(upPressed){
              daigo.jump();
            }
          }

          // 土管で死んだ時の演出
          if(player.Death3()){
            g.drawImage(img3, (int)player.getX() + offsetX - 100,0 ,this);
            g.drawImage(img4,150,200,this);
          }
          // 土管を再描画
          if(downPressed){
            if(player.onDokan()){
              map.dokanDraw(g, (int)player.getX() + offsetX - 30, 410 - offsetY);
            }
          }
          if(player.Death5()){ // 酒で死んだときの処理

            player.draw(g, offsetX, offsetY);
          }
          if(clearString){ // クリアして数秒後
            Font font2 = new Font("MS Pゴシック", Font.PLAIN,30);
            g.setColor(Color.BLACK);
            g.setFont(font2);
            g.drawString("ゲームクリアー",280, 250);
            g.drawString("プレイしてくれてありがとー", 200, 300);
          }
          break;
        }
      }
      public void update(Graphics g){ //repaint()に呼ばれる
        paint(g);
      }

        /**
       * キーボード操作
       * ボタンを押したときの処理
       */
      public void keyPressed(KeyEvent e) {
          int key = e.getKeyCode();

          if (key == KeyEvent.VK_LEFT) {
              leftPressed = true;
              downPressed = false;
          }
          if (key == KeyEvent.VK_RIGHT) {
              rightPressed = true;
              downPressed = false;
          }
          if (key == KeyEvent.VK_UP) {
              upPressed = true;
              downPressed = false;
          }
          if (key == KeyEvent.VK_DOWN) {
              downPressed = true;
          }
          if (key == KeyEvent.VK_SPACE) {
              spacePressed = true;
              downPressed = false;
              if (this.mode == -2) {
                this.mode = -1;
              }
          repaint();
          }
      }

      /**
       * キーボードでの操作
       * ボタンを離した時の処理
       */
      public void keyReleased(KeyEvent e) {
          int key = e.getKeyCode();

          if (key == KeyEvent.VK_LEFT) {
              leftPressed = false;
          }
          if (key == KeyEvent.VK_RIGHT) {
              rightPressed = false;
          }
          if (key == KeyEvent.VK_UP) {
              upPressed = false;
          }
          if (key == KeyEvent.VK_DOWN) {
              downPressed = true;
          }
          if (key == KeyEvent.VK_SPACE) {
              spacePressed = false;
          }
      }
      /**
       * @return downPressedの値をかえす
       */
      public static boolean down() {
        return downPressed;
      }

      public void keyTyped(KeyEvent e) {
      }

}
