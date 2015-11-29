package yitgogo.smart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class AutoLoadScrollView extends ScrollView {

	public AutoLoadScrollView(Context paramContext) {
		super(paramContext);
	}

	public AutoLoadScrollView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
	}

}
