package me.mlshv.simpletranslate.network;

import me.mlshv.simpletranslate.data.model.Translation;


public class TranslationTaskResult {
    private Translation result;
    private Exception exception;

    public TranslationTaskResult(Translation result) {
        this.result = result;
    }

    public TranslationTaskResult(Exception exception) {
        this.exception = exception;
    }

    public Translation getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }
}
