package ir.amulay.tabeta.tourguide;

import ir.amulay.tabeta.activities.GameActivity;
import android.graphics.Typeface;
import android.view.View;

/**
 * {@link ChainTourGuide} is designed to be used with {@link Sequence}. The purpose is to run TourGuide in a series.
 * {@link ChainTourGuide} extends from {@link TourGuide} with extra capability to be run in sequence.
 * Check OverlaySequenceTourActivity.java in the Example of TourGuide to learn how to use.
 */
public class ChainTourGuide extends TourGuide {
    private Sequence mSequence;

    public ChainTourGuide(GameActivity activity) {
        super(activity);
    }

    /* Static builder */
    public static ChainTourGuide init(GameActivity activity){
        return new ChainTourGuide(activity);
    }

    @Override
    public TourGuide playOn(View targetView,Typeface tf) {
        throw new RuntimeException("playOn() should not be called ChainTourGuide, ChainTourGuide is meant to be used with Sequence. Use TourGuide class for playOn() for single TourGuide. Only use ChainTourGuide if you intend to run TourGuide in consecutively.");
    }

    public ChainTourGuide playLater(View view){
        mHighlightedView = view;
        return this;
    }

    @Override
    public ChainTourGuide with(Technique technique) {
        return (ChainTourGuide)super.with(technique);
    }

    @Override
    public ChainTourGuide motionType(MotionType motionType) {
        return (ChainTourGuide)super.motionType(motionType);
    }

    @Override
    public ChainTourGuide setOverlay(Overlay overlay) {
        return (ChainTourGuide)super.setOverlay(overlay);
    }

    @Override
    public ChainTourGuide setToolTip(ToolTip toolTip) {
        return (ChainTourGuide)super.setToolTip(toolTip);
    }

    @Override
    public ChainTourGuide setPointer(Pointer pointer) {
        return (ChainTourGuide)super.setPointer(pointer);
    }

    public ChainTourGuide next(Typeface tf){
        if (mFrameLayout!=null) {
            cleanUp();
        }

        if (mSequence.mCurrentSequence < mSequence.mTourGuideArray.length) {
            setToolTip(mSequence.getToolTip());
            setPointer(mSequence.getPointer());
            setOverlay(mSequence.getOverlay());

            mHighlightedView = mSequence.getNextTourGuide().mHighlightedView;

            setupView(tf);
            mSequence.mCurrentSequence++;
        }
        return this;
    }

    /**************************
     * Sequence related method
     **************************/

    public ChainTourGuide playInSequence(Sequence sequence,Typeface tf){
        setSequence(sequence,tf);
        next(tf);
        return this;
    }

    public ChainTourGuide setSequence(Sequence sequence,Typeface tf){
        mSequence = sequence;
        mSequence.setParentTourGuide(this,tf);
        for (ChainTourGuide tourGuide : sequence.mTourGuideArray){
            if (tourGuide.mHighlightedView == null) {
                throw new NullPointerException("Please specify the view using 'playLater' method");
            }
        }
        return this;
    }
}
