package com.tools.security.bean;

import com.tools.security.R;

import static com.tools.security.applock.view.lock.GestureCreateActivity.ID_EMPTY_MESSAGE;

/**
 * Created by lzx on 2017/1/8.
 * 图案锁的状态
 */

public enum LockStage {
    Introduction(R.string.lock_recording_intro_header, ID_EMPTY_MESSAGE, true),

    HelpScreen(R.string.lock_settings_help_how_to_record, ID_EMPTY_MESSAGE, false),

    ChoiceTooShort(R.string.lock_recording_incorrect_too_short, ID_EMPTY_MESSAGE, true),

    FirstChoiceValid(R.string.lock_pattern_entered_header, ID_EMPTY_MESSAGE, false),

    NeedToConfirm(R.string.lock_need_to_confirm, ID_EMPTY_MESSAGE, true),

    ConfirmWrong(R.string.lock_need_to_unlock_wrong, ID_EMPTY_MESSAGE, true),

    ChoiceConfirmed(R.string.lock_pattern_confirmed_header, ID_EMPTY_MESSAGE, false);

    /**
     * @param headerMessage  The message displayed at the top.
     * @param footerMessage  The footer message.
     * @param patternEnabled Whether the pattern widget is enabled.
     */
    LockStage(int headerMessage, int footerMessage,
              boolean patternEnabled) {
        this.headerMessage = headerMessage;
        this.footerMessage = footerMessage;
        this.patternEnabled = patternEnabled;
    }

    public final int headerMessage;
    public final int footerMessage;
    public final boolean patternEnabled;
}
