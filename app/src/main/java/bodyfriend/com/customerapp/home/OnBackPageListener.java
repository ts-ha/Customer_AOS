package bodyfriend.com.customerapp.home;

/**
 * Created by 이주영 on 2017-03-06.
 *
 * 액비티에서 웹뷰가 onBack이 됐을때
 * onBack가능 여부를 리턴
 *
 * true를 리턴하면 goBack()하지 않는다.
 */

public interface OnBackPageListener {
    boolean onBack();
}
