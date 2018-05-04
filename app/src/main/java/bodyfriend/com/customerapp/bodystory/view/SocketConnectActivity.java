package bodyfriend.com.customerapp.bodystory.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import bodyfriend.com.customerapp.R;
import bodyfriend.com.customerapp.bodystory.presenter.SocketConnectPresenter;
import bodyfriend.com.customerapp.bodystory.presenter.SocketConnectPresenterImpl;
import bodyfriend.com.customerapp.databinding.ActivitySocketConnectBinding;

public class SocketConnectActivity extends BaseActivity implements SocketConnectPresenter.View {

    private ActivitySocketConnectBinding binding;
    private SocketConnectPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_socket_connect);
        presenter = new SocketConnectPresenterImpl(this);

        setupClickEvents();

        presenter.start();
        // TODO: 2017. 11. 15. test
//        presenter.connect("rell", "172.30.40.31", "10001");
    }


    private void setupClickEvents() {
        binding.connect.setOnClickListener(v -> {
            String nick = binding.nick.getText().toString();
            String host = binding.host.getText().toString();
            String port = binding.port.getText().toString();
            presenter.connect(nick, host, port);
        });
    }

    @Override
    public void errorNick(String s) {
        binding.nick.setError(s);
    }

    @Override
    public void errorHost(String s) {
        binding.host.setError(s);
    }

    @Override
    public void errorPort(String s) {
        binding.port.setError(s);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void completed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void showProgress() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progress.setVisibility(View.GONE);
    }
}
