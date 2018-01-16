## ConcealerNestedScrollView

A library to make views hide from top and bottom while scrolling
a custom NestedScrollView.

## usage

#### XML
Starting with your XML layout, it should look like this:

![concealernsv-layout-setup](https://user-images.githubusercontent.com/24822099/34965249-ea66cfca-fa67-11e7-9982-20bf76e61551.png)

A parent `RelativeLayout` or `FrameLayout` that inside it is the
`ConcealerNestedScrollView` on top, and two views (or one) as
header and footer  below it.

__IMPORTANT:__ DO NOT give `margin_top` to header view,
or `margin_bottom` to footer view. We'll do that in Java.

Click [here](createLinkHere) to see a sample.

#### JAVA

In your java first get a reference to the `ConcealerNestedScrollView`
widget. (also get a reference to the header and footer views as well).

``` JAVA
        ConcealerNestedScrollView cnsv = findViewById(R.id.cnsv);
```

Then you should pass the `headerView` and `footerView` (and margin top
for header and margin bottom for footer views) to the `cnsv` object.

To pass these views, you should make sure that they are completely
drawn on the screen, so the library would be able to get their sizes.

In order to do so, in your `onCreate` call `.post()` on header and footer
views:

``` JAVA
        headerView.post(new Runnable() {
            @Override
            public void run() {
                // parameters are (View headerView, int marginTop)
                cnsv.setHeaderView(headerView, 15);
            }
        });
        footerView.post(new Runnable() {
            @Override
            public void run() {
                // parameters are (View footerView, int marginBottom)
                cnsv.setFooterView(footerView, 0);
            }
        });
```

__IMPORTANT NOTES:__

* if you dont want to set header and footer views
from inside the onCreate, or you are sure when you want to pass
header or footer views, they are drawn on the screen, dont call
`.post()` on the views, and directly pass the views.

* if on runtime your `headerView` height size changes (either by
directly changing it's size, or setting a view inside it to `VISIBLE`
that causes the view's height size to change), make sure to call
`cnsv.resetHeaderHeight();` exactly right after the code for
resizing has been run. Likewise for the `footerView`, call
`cnsv.resetFooterHeight();`.

* You can make the views hide twice as fast if the
`cnsv` has been scrolled more than the view's heights, by calling
`cnsv.setHeaderFastHide(true);` and/or `cnsv.setFooterFastHide(true);`.