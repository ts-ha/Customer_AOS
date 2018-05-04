package bodyfriend.com.customerapp.bodystory.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import bodyfriend.com.customerapp.R;
import bodyfriend.com.customerapp.bodystory.presenter.GameStartPresenter;
import bodyfriend.com.customerapp.bodystory.presenter.GameStartPresenterImpl;
import bodyfriend.com.customerapp.databinding.ActivityGameStart2Binding;


public class GameStartActivity extends BaseActivity implements GameStartPresenter.View {

    private static final int CONNECT_CODE = 1001;
    private ActivityGameStart2Binding binding;
    private GameStartPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_start2);
        presenter = new GameStartPresenterImpl(this);

        // button event
        setupButtonEvent();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setupButtonEvent() {
        binding.start.setOnClickListener(v -> presenter.clickStart());
    }


    @Override
    public void startGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }


    @Override
    public void showSocketSettingPopup() {
        Intent intent = new Intent(this, SocketConnectActivity.class);
        startActivityForResult(intent, CONNECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CONNECT_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            presenter.connectOk();
        }
    }
}
