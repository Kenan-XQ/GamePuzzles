package newsimooc.imooc.com.gamepuzzles;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import newsimooc.imooc.com.view.GameLayout;

public class MainActivity extends AppCompatActivity {

    private GameLayout mGameLayout;
    private TextView mLevel;
    private TextView mTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLevel = (TextView) findViewById(R.id.id_level);
        //mTime = (TextView) findViewById(R.id.id_time);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("拼图游戏规则")
                .setMessage("本游戏一共十个难度，据说没人见到过最后一关。\n所以，加油吧，少年！\n")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "游戏开始！", Toast.LENGTH_SHORT).show();
                    }
                }).show();


        mGameLayout = (GameLayout) findViewById(R.id.id_game);
        mGameLayout.setEnabled(true);
        mGameLayout.setOnGameListener(new GameLayout.GameListener() {
            @Override
            public void nextLevel(final int nextLevel) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("结束界面")
                        .setMessage("难度升级！！！")
                        .setPositiveButton("下一关", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGameLayout.nextLevel();
                                //Log.d("MainActivity", "level = " + nextLevel);
                                mLevel.setText("" + nextLevel);
                            }
                        }).show();

            }

            @Override
            public void timechanged(int currentTime) {
                Log.d("MainActivity", "时间更改");
                mTime.setText("" + currentTime);
            }

            @Override
            public void gameover() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("结束界面")
                        .setMessage("时间结束！Game Over！！！")
                        .setPositiveButton("重新开始本关卡", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGameLayout.nextLevel();
                            }
                        })
                        .setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }
}
