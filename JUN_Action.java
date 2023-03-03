import java.awt.*;
import javax.swing.*;

public class JUN_Action extends JFrame {
    public JUN_Action() {
        // タイトルを設定
        setTitle("じゅんぺいのアクション");

        // メインパネルを作成してフレームに追加
        MainPanel panel = new MainPanel();
        Container contentPane = getContentPane();
        contentPane.add(panel);

        // パネルサイズに合わせてフレームサイズを自動設定
        pack();
    }
    //(自分用)main()の1行目ではJUN_Actionフレームのオブジェクトを作っている
    public static void main(String[] args) {
        JUN_Action frame = new JUN_Action();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
