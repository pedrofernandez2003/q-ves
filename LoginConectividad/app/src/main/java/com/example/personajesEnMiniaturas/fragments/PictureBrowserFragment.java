package com.example.personajesEnMiniaturas.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.R;
import com.example.listeners.imageIndicatorListener;
import com.example.personajesEnMiniaturas.utils.PictureFacer;
import com.example.personajesEnMiniaturas.utils.RecyclerViewPagerImageIndicator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

import static androidx.core.view.ViewCompat.setTransitionName;


public class PictureBrowserFragment extends Fragment implements imageIndicatorListener {

    private  ArrayList<PictureFacer> allImages = new ArrayList<>();
    private int position;
    private Context animeContx;
    private ImageView image;
    private ViewPager imagePager;
    private RecyclerView indicatorRecycler;
    private int viewVisibilityController;
    private int viewVisibilitylooper;
    private ImagesPagerAdapter pagingImages;
    private int previousSelected = -1;

    public ArrayList<PictureFacer> getAllImages() {
        return allImages;
    }

    public void setAllImages(ArrayList<PictureFacer> allImages) {
        this.allImages = allImages;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Context getAnimeContx() {
        return animeContx;
    }

    public void setAnimeContx(Context animeContx) {
        this.animeContx = animeContx;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public ViewPager getImagePager() {
        return imagePager;
    }

    public void setImagePager(ViewPager imagePager) {
        this.imagePager = imagePager;
    }

    public RecyclerView getIndicatorRecycler() {
        return indicatorRecycler;
    }

    public void setIndicatorRecycler(RecyclerView indicatorRecycler) {
        this.indicatorRecycler = indicatorRecycler;
    }

    public int getViewVisibilityController() {
        return viewVisibilityController;
    }

    public void setViewVisibilityController(int viewVisibilityController) {
        this.viewVisibilityController = viewVisibilityController;
    }

    public int getViewVisibilitylooper() {
        return viewVisibilitylooper;
    }

    public void setViewVisibilitylooper(int viewVisibilitylooper) {
        this.viewVisibilitylooper = viewVisibilitylooper;
    }

    public ImagesPagerAdapter getPagingImages() {
        return pagingImages;
    }

    public void setPagingImages(ImagesPagerAdapter pagingImages) {
        this.pagingImages = pagingImages;
    }

    public int getPreviousSelected() {
        return previousSelected;
    }

    public void setPreviousSelected(int previousSelected) {
        this.previousSelected = previousSelected;
    }

    public PictureBrowserFragment(){

    }

    public PictureBrowserFragment(ArrayList<PictureFacer> allImages, int imagePosition, Context anim) {
        this.allImages = allImages;
        this.position = imagePosition;
        this.animeContx = anim;
    }

    public static PictureBrowserFragment newInstance(ArrayList<PictureFacer> allImages, int imagePosition, Context anim) {
        PictureBrowserFragment fragment = new PictureBrowserFragment(allImages,imagePosition,anim);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.picture_browser, container, false);


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initialisation of the recyclerView visibility control integers
        viewVisibilityController = 0;
        viewVisibilitylooper = 0;
        //setting up the viewPager with images
        imagePager = view.findViewById(R.id.imagePager);
        pagingImages = new ImagesPagerAdapter();
        imagePager.setAdapter(pagingImages);
        imagePager.setOffscreenPageLimit(3);
        imagePager.setCurrentItem(position);
        indicatorRecycler = view.findViewById(R.id.indicatorRecycler);
        indicatorRecycler.hasFixedSize();
        RecyclerView.Adapter indicatorAdapter = new RecyclerViewPagerImageIndicator(allImages,getContext(),this);
        indicatorRecycler.setAdapter(indicatorAdapter);
        allImages.get(position).setSelected(true);
        previousSelected = position;
        indicatorAdapter.notifyDataSetChanged();

        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(previousSelected != -1){
                    allImages.get(previousSelected).setSelected(false);
                    previousSelected = position;
                    allImages.get(position).setSelected(true);
                    indicatorRecycler.getAdapter().notifyDataSetChanged();
                }else{
                    previousSelected = position;
                    allImages.get(position).setSelected(true);
                    indicatorRecycler.getAdapter().notifyDataSetChanged();
                }
                setPosition(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onImageIndicatorClicked(int ImagePosition) {

        if(previousSelected != -1){
            allImages.get(previousSelected).setSelected(false);
            previousSelected = ImagePosition;
            indicatorRecycler.getAdapter().notifyDataSetChanged();
        }else{
            previousSelected = ImagePosition;
        }

        imagePager.setCurrentItem(ImagePosition);
    }

    private class ImagesPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return allImages.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup containerCollection, int position) {
            LayoutInflater layoutinflater = (LayoutInflater) containerCollection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutinflater.inflate(R.layout.picture_browser_pager,null);
               image = view.findViewById(R.id.image);

            setTransitionName(image, String.valueOf(position)+"picture");

            PictureFacer pic = allImages.get(position);
            Glide.with(animeContx)
                    .load(pic.getPicturePath())
                    .apply(new RequestOptions().fitCenter())
                    .into(image);


            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(indicatorRecycler.getVisibility() == View.GONE){
                        indicatorRecycler.setVisibility(View.VISIBLE);
                    }else{
                        indicatorRecycler.setVisibility(View.GONE);
                    }

                }
            });



            ((ViewPager) containerCollection).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup containerCollection, int position, Object view) {
            ((ViewPager) containerCollection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((View) object);
        }
    }


}
