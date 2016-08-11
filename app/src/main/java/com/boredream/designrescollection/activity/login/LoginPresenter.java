package com.boredream.designrescollection.activity.login;

import android.content.Context;
import android.text.TextUtils;

import com.boredream.bdcodehelper.net.ObservableDecorator;
import com.boredream.bdcodehelper.utils.ErrorInfoUtils;
import com.boredream.designrescollection.entity.User;
import com.boredream.designrescollection.net.HttpRequest;
import com.boredream.designrescollection.net.SimpleSubscriber;

import rx.Observable;

public class LoginPresenter implements LoginContract.Presenter {

    private Context context;
    private final LoginContract.View loginView;

    public LoginPresenter(Context context, LoginContract.View loginView) {
        this.context = context;
        this.loginView = loginView;
        this.loginView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void login(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            loginView.loginError("用户名不能为空");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            loginView.loginError("密码不能为空");
            return;
        }

        loginView.showProgress();

        Observable<User> observable = HttpRequest.login(username, password);
        ObservableDecorator.decorate(observable).subscribe(
                new SimpleSubscriber<User>(context) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        loginView.dismissProgress();

                        String error = ErrorInfoUtils.parseHttpErrorInfo(e);
                        loginView.loginError(error);
                    }

                    @Override
                    public void onNext(User user) {
                        loginView.dismissProgress();

                        loginView.loginSuccess(user);
                    }
                });
    }
}
