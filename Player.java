import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import java.util.concurrent.*;
import java.awt.Image;
import javax.swing.ImageIcon;


public class Player{
    // 幅
    public static final int WIDTH = 38;
    // 高さ
    public static final int HEIGHT = 38;
    // スピード
    private static final int SPEED = 6;
    // ジャンプ力
    private static int JUMP_SPEED = 14;
    // 重力
    private static double GRAVITY = 1.0;
    // 加速度
    private static double vv;
    // 位置
    public static double x;
    public static double y;

    // 速度
    private double vx;
    private double vy;
    // 加速中かどうか
    private boolean ACCEL = false;

    // 着地しているか
    private boolean onGround;
    // ヒントブロックをたたいたか
    private boolean isHinto;
    // 酒をとってしまったか
    private boolean isSake;
    // きのこをとってしまったか
    private boolean isKinoko;
    // 土管の上かどうか
    private boolean onDokan;
    // 土管Soundが鳴ったかどうかに使う
    private boolean yetDokan;
    // ゴールしたか
    private boolean isGoal;
    // ゴールsleep用の旗
    private boolean isGoal_;
    // ゴール音楽用の旗
    private boolean GoalMusic;
    // ゴール後のじゅんぺい
    private boolean goalend;

    // プレイヤー画像
    private Image image;
    // プレイヤー2画像
    private Image img2;
    // プレイヤー3画像
    private Image img3;
    // プレイヤー4画像
    private Image img4;

    // マップへの参照
    private Map map;
    // ジャンプする音
    private AudioClip jumpSound;
    // 踏む音
    private AudioClip humiSound;
    // 土管に入る音
    private AudioClip dokanSound;
    // 土管に飛ばされる音
    private AudioClip frySound;
    // ゴールした時の音
    private AudioClip goalSound;
    // きのこをとった時の音
    private AudioClip kinokoSound;


    public Player (double x, double y, Map map) {
        this.x = x;
        this.y = y;
        this.map = map;
        vx = 0;
        vy = 0;
        onGround = false;
        onDokan = false;
        yetDokan = true;
        isGoal = false;
        isGoal_ = true;
        GoalMusic = true;
        goalend = false;
        isHinto = false;
        isSake = false;
        isKinoko = false;
         // イメージをロードする
        loadImage();
        // サウンドをダウンロード
        jumpSound = Applet.newAudioClip(getClass().getResource("SE/jump.wav"));
        humiSound = Applet.newAudioClip(getClass().getResource("SE/humi.wav"));
        frySound = Applet.newAudioClip(getClass().getResource("SE/fry.wav"));
        dokanSound = Applet.newAudioClip(getClass().getResource("SE/dokan.wav"));
        goalSound =  Applet.newAudioClip(getClass().getResource("SE/goal.wav"));
        kinokoSound =  Applet.newAudioClip(getClass().getResource("SE/powerup.wav"));
        // アニメーション用スレッドを開始
        //AnimationThread thread = new AnimationThread();
        //thread.start();
    }
    /**
     *停止
     */

    public void stop() {
      if(vx == 0.0){ // 止まっていた時
        vx = 0;
      }

      if(vx>0){ // 右向きに動いていた時
        vx -=1.0;
        if(vx<=0){
          vx = 0;
        }
      }

      if(vx<0){ // 左に動いていた時
        vx += 1.0;
        if(vx>=0){
          vx = 0;
        }
      }
    }

    /**
     *左に移動
     */
    public void accelerateLeft() {
        vv += 0.1;
        vx = vx-vv;
        if(vx <= -SPEED){
          vx = -SPEED;
          vv = 0;
        }

    }

    /**
     *右に移動
     */
    public void accelerateRight() {
        vv += 0.1;
        vx = vx + vv;
        if(vx >= SPEED){
          vx = SPEED;
          vv = 0;
        }
    }

    /**
    * ジャンプする
    */
   public void jump() {

       if (onGround) {
         // ジャンプ音を出す
          jumpSound.play();

          vy = -JUMP_SPEED;
          // 上向きに速度を加える
       }
       // 押し続けているときの重力を軽減する（大ジャンプの実装）
       vy += GRAVITY-1.5;
       // 跳んでいるときは空中
       onGround = false;

    }

    public void indokan(){ // 土管に入るとふっとばされる
      if(onDokan){ // 土管の上ならば
        if(yetDokan){ // この処理がないと土管Soundが連続して何度も鳴る
          dokanSound.play();
          yetDokan = false;
        }
        vx = 0;
        vy = 0.1; // 土管に入る

        if(y >= 630){ // ある程度土管に潜ったら
          frySound.play();
          vy = -50;
          y = 472;
          x = 1290; //この位置から上に飛ばす
        }
      }

    }

    /*
     * 死
     */
     public boolean Death() {
       if(y == Map.HEIGHT - this.HEIGHT - 30){ // 画面端にいくと謎のエラーが出るので、そこにいくまでに死ぬよう−30した
         return true;
       }else{
         return false;
       }
     }

     // enemyとぶつかったときの死
     public boolean Death2(Enemy enemy){
       if((Math.abs(this.x - enemy.getX()) < (this.WIDTH + Enemy.WIDTH)/2) && (Math.abs(this.y - enemy.getY()) < (this.HEIGHT + Enemy.HEIGHT)/2)) {

         if ((int)this.y < (int)enemy.getY()) { // 上から踏まれていたら
           vy = -JUMP_SPEED;
           enemy.revive(enemy.WIDTH, enemy.HEIGHT);
           humiSound.play();
           return false;
         }
         else{
           return true;
         }
       }
       else{
         return false;
       }
     }

     // 上に行き過ぎたときの死
      public boolean Death3(){
        if(y <= 30){
          return true;
        }else{
          return false;
        }
      }

      // だいごと当たった時の死
      public boolean Death4(Daigo daigo){
        if((Math.abs(this.x - daigo.getX()) < (this.WIDTH + daigo.WIDTH)/2) && (Math.abs(this.y - daigo.getY()) < (this.HEIGHT + daigo.HEIGHT)/2)) {

          if ((int)this.y < (int)daigo.getY()) { // 上から踏まれていたら
            vy = -JUMP_SPEED;
            daigo.revive(daigo.WIDTH, daigo.HEIGHT);
            humiSound.play();
            return false;
          }
          else{
            return true;
          }
        }
        else{
          return false;
        }
      }

      // 酒をとってしまった時の死
      public boolean Death5(){
        return isSake;
      }
      //ゴールしたときの動作
      public void Goal(){
        if(isGoal){ // ゴールしたなら
          vy = 5.0;
          vx = 10;
          if(GoalMusic){ //これがないと連続して音が鳴り続ける
            goalSound.play();
          }
          GoalMusic = false;
          if(onGround){ // 旗から降りたとき少し停止する
            if(isGoal_){ // 一度しかsleepを使わないので使ったらfalse
              try{
                Thread.sleep(1000);
              } catch(InterruptedException e)
              {
                System.out.println("try-catchが必要です。");
              }
              isGoal_ = false;
            }
            vx = 5.0; // ゴール後右に移動
            vy += GRAVITY;
            if(x >= 3840){
              x = 3840;
              y = 690;
              vx = -4.0; // ある程度進んだら止まる
              goalend = true;
            }
          }
        }
      }

    /**
     *プレイヤーの状態を更新
     */
    public void update() {
      // 重力で下向きに加速度がかかる
        vy += GRAVITY;

        /**
        *X方向の当たり判定
        */
         double newX = x + vx;
         Point tile = map.getTileCollision(this, newX, y);

         if(tile == null){ //衝突するタイルがなければ
           x = newX;
         }else{ // 衝突するタイルがある場合
           if(vx >= 0){ // 右向きに衝突
           x = Map.tilesToPixels(tile.x) - WIDTH; // 位置調整
              if(map.isGoal(tile.x, tile.y)){ // ゴールしたなら
                isGoal = true;
              }
              if(map.isSake(tile.x, tile.y)){ // 酒をとってしまったら
                map.changeNull(tile.x, tile.y); // 酒は消える
                isSake = true;
              }
              if(map.isKinoko(tile.x, tile.y)){ // きのこをとってしまったら
                map.changeNull(tile.x, tile.y); // きのこは消える
                kinokoSound.play();
                isKinoko = true;
              }
         }else{ // 左向きに衝突
             x = Map.tilesToPixels(tile.x + 1);
             if(map.isSake(tile.x, tile.y)){ // 酒をとってしまったら
               map.changeNull(tile.x, tile.y); // 酒は消える
               isSake = true;
             }
             if(map.isKinoko(tile.x, tile.y)){ // きのこをとってしまったら
               map.changeNull(tile.x, tile.y); // きのこは消える
               kinokoSound.play();
               isKinoko = true;
             }
           }
           vx = 0;
         }

     /**
      *Y方向の当たり判定
      */
       double newY = y + vy;
       tile = map.getTileCollision(this, x, newY);
      if (tile == null) {  // 衝突するタイルがなければ
          y = newY;  // 衝突してないということは空中
          onGround = false;
          onDokan = false;
      }else{ // 衝突するタイルがある場合
          if (vy > 0) { // 下へ移動中なので下のブロックと衝突（着地）
              if(map.isDokan(tile.x, tile.y)){ // 土管の場合
                onDokan = true;
                onGround = true;
                if(MainPanel.down()){ // 土管の真ん中から土管に入るようにする
                  x = 1290;
                  y = newY;
                }
              }else{
              // 位置調整
              y = Map.tilesToPixels(tile.y) - HEIGHT;
              // 着地したのでy方向速度を0に
              vy = 0;
              // 着地
              onGround = true;
              if(isKinoko){ // きのこを取った後は着地したどんなブロックも破壊
                 map.BreakBrickBlock(tile.x, tile.y);
                 map.BreakBrickBlock(tile.x+1, tile.y);
                 map.BreakBrickBlock(tile.x-1, tile.y);
                 onGround = false; // きのこをとった後はジャンプが許されない(空中にいることにする)
              }
            }
          } else if(vy<0){ // 上へ移動中なので上のブロックと衝突
              // 位置調整
              y = Map.tilesToPixels(tile.y + 1);
              // 天井にぶつかったのでy方向速度を0に
              vy = 0;
              // レンガブロックの場合
              if (map.isBrickBlock(tile.x, tile.y)) {
                   map.BreakBrickBlock(tile.x, tile.y);
              }else if (map.isBrickBlock(tile.x + 1, tile.y)) {
                   // 1つ右側のブロックも叩いていることにする
                   // この処理がないとブロックがちょっと叩きづらい
                   map.BreakBrickBlock(tile.x + 1, tile.y);
              }
              // はてなブロックの場合
              if (map.isHatenaBlock(tile.x, tile.y)) {
                   map.changeBlock(tile.x, tile.y);
                   map.sake(tile.x, tile.y);
              }else if (map.isHatenaBlock(tile.x + 1, tile.y)) {
                   // 1つ右側のブロックも叩いていることにする
                   // この処理がないとブロックがちょっと叩きづらい
                   map.changeBlock(tile.x + 1, tile.y);
                   map.sake(tile.x, tile.y);
              }
              // はてなブロック2の場合
              if (map.isHatena2Block(tile.x, tile.y)) {
                   map.changeBlock(tile.x, tile.y);
                   map.kinoko(tile.x, tile.y);
              }else if (map.isHatena2Block(tile.x + 1, tile.y)) {
                   // 1つ右側のブロックも叩いていることにする
                   // この処理がないとブロックがちょっと叩きづらい
                   map.changeBlock(tile.x + 1, tile.y);
                   map.kinoko(tile.x, tile.y);
              }
              // ヒントブロックの場合
              if (map.isHintoBlock(tile.x, tile.y)) {

                  map.Hinto(tile.x, tile.y);
              }else if (map.isHintoBlock(tile.x + 1, tile.y)) {

                  map.Hinto(tile.x, tile.y);
              }
              // 隠しブロックだった場合
              if(map.isHideBlock(tile.x, tile.y)){
                map.changeBlock(tile.x, tile.y);
                map.HideString(tile.x + 1, tile.y);
              }else if(map.isHideBlock(tile.x + 1, tile.y)){
                map.changeBlock(tile.x + 1, tile.y);
                map.HideString(tile.x + 2, tile.y);
              }

          }
      }
  }



    /**
     * イメージをロードする
     */
    private void loadImage() {
        ImageIcon icon = new ImageIcon(getClass().getResource("img/Player.png"));
        ImageIcon icon2 = new ImageIcon(getClass().getResource("img/jun4.png"));
        ImageIcon icon3 = new ImageIcon(getClass().getResource("img/jun5.png"));
        ImageIcon icon4 = new ImageIcon(getClass().getResource("img/jun2.png"));
        image = icon.getImage();
        img2 = icon2.getImage();
        img3 = icon3.getImage();
        img4 = icon4.getImage();
    }
    /**
     *プレイヤーを描画
     */
    public void draw(Graphics g, int offsetX, int offsetY) {
        g.drawImage(image, (int)x + offsetX, (int)y + offsetY, Player.WIDTH, Player.HEIGHT, null);
        if(isSake){
          g.setColor(Color.cyan);
          g.fillRect((int)x + offsetX, (int)y + offsetY,38,38);
          g.drawImage(img2, (int)x + offsetX - 40, (int)y + offsetY - 20, Player.WIDTH + 50, Player.HEIGHT + 50, null);
          g.setColor(Color.BLACK);
          g.drawString("飲みすぎた....", (int)x + offsetX + 80, (int)y + offsetY);
        }
        if(isKinoko){
          g.setColor(Color.cyan);
          g.fillRect((int)x + offsetX, (int)y + offsetY,38,38);
          g.drawImage(img3, (int)x + offsetX - 40, (int)y + offsetY - 20, Player.WIDTH + 50, Player.HEIGHT + 50, null);
        }
        if(x>=3840){
          g.setColor(Color.cyan);
          g.fillRect((int)x + offsetX, (int)y + offsetY,38,38);
          g.drawImage(img4, (int)x + offsetX - 40, (int)y + offsetY - 50, Player.WIDTH + 50, Player.HEIGHT + 50, null);
        }
    }
    /**
     *burstを描画
     *
    public void burstdraw(Graphics g) {
        g.drawImage(image, 1100, 0, null);
    }



    /**
     * @return xの値をかえす
     */
    public double getX() {
      return x;
    }
    /**
     * @return yの値をかえす
     */
    public double getY() {
      return y;
    }
    /**
     * @return onDokanの値をかえす
     */
    public boolean onDokan() {
      return onDokan;
    }

    /**
     * @return goalendの値をかえす
     */
    public boolean goalend() {
      return goalend;
    }
}
