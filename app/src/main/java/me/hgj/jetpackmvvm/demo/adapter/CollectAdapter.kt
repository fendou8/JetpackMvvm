package me.hgj.jetpackmvvm.demo.adapter

import android.text.Html
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.hgj.jetpackmvvm.demo.R
import me.hgj.jetpackmvvm.demo.app.ext.setAdapterAnimion
import me.hgj.jetpackmvvm.demo.app.util.SettingUtil
import me.hgj.jetpackmvvm.demo.app.weight.customview.CollectView
import me.hgj.jetpackmvvm.demo.data.bean.CollectResponse


class CollectAdapter(data: ArrayList<CollectResponse>) :
    BaseDelegateMultiAdapter<CollectResponse, BaseViewHolder>(data) {
    private var mOnCollectViewClickListener: OnCollectViewClickListener? = null
    private val Ariticle = 1//文章类型
    private val Project = 2//项目类型 本来打算不区分文章和项目布局用统一布局的，但是布局完以后发现差异化蛮大的，所以还是分开吧

    init {

        setAdapterAnimion(SettingUtil.getListMode())

        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<CollectResponse>() {
            override fun getItemType(data: List<CollectResponse>, position: Int): Int {
                //根据是否有图片 判断为文章还是项目，好像有点low的感觉。。。我看实体类好像没有相关的字段，就用了这个，也有可能是我没发现
                return if (TextUtils.isEmpty(data[position].envelopePic)) Ariticle else Project
            }
        })
        // 第二步，绑定 item 类型
        getMultiTypeDelegate()?.let {
            it.addItemType(Ariticle, R.layout.item_ariticle)
            it.addItemType(Project, R.layout.item_project)
        }
    }

    override fun convert(helper: BaseViewHolder, item: CollectResponse) {
        when (helper.itemViewType) {
            Ariticle -> {
                //文章布局的赋值
                item.run {
                    helper.setText(R.id.item_home_author, if (author.isEmpty()) "匿名用户" else author)
                    helper.setText(R.id.item_home_content, Html.fromHtml(title))
                    helper.setText(R.id.item_home_type2, Html.fromHtml(chapterName))
                    helper.setText(R.id.item_home_date, niceDate)
                    helper.getView<CollectView>(R.id.item_home_collect).isChecked = true
                    //隐藏所有标签
                    helper.setGone(R.id.item_home_top, true)
                    helper.setGone(R.id.item_home_type1, true)
                    helper.setGone(R.id.item_home_new, true)
                }
                helper.getView<CollectView>(R.id.item_home_collect)
                    .setOnCollectViewClickListener(object : CollectView.OnCollectViewClickListener {
                        override fun onClick(v: CollectView) {
                            mOnCollectViewClickListener?.onClick(item, v, helper.adapterPosition)
                        }
                    })
            }
            Project -> {
                //项目布局的赋值
                item.run {
                    helper.setText(
                        R.id.item_project_author,
                        if (author.isEmpty()) "匿名用户" else author
                    )
                    helper.setText(R.id.item_project_title, Html.fromHtml(title))
                    helper.setText(R.id.item_project_content, Html.fromHtml(desc))
                    helper.setText(R.id.item_project_type, Html.fromHtml(chapterName))
                    helper.setText(R.id.item_project_date, niceDate)
                    //隐藏所有标签
                    helper.setGone(R.id.item_project_top, true)
                    helper.setGone(R.id.item_project_type1, true)
                    helper.setGone(R.id.item_project_new, true)
                    helper.getView<CollectView>(R.id.item_project_collect).isChecked = true
                    Glide.with(context.applicationContext).load(envelopePic)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(helper.getView(R.id.item_project_imageview))
                }
                helper.getView<CollectView>(R.id.item_project_collect)
                    .setOnCollectViewClickListener(object : CollectView.OnCollectViewClickListener {
                        override fun onClick(v: CollectView) {
                            mOnCollectViewClickListener?.onClick(item, v, helper.adapterPosition)
                        }
                    })
            }
        }


    }

    fun setOnCollectViewClickListener(onCollectViewClickListener: OnCollectViewClickListener) {
        mOnCollectViewClickListener = onCollectViewClickListener
    }


    interface OnCollectViewClickListener {
        fun onClick(item: CollectResponse, v: CollectView, position: Int)
    }
}


