package com.slearn.membermanagement.support;

import com.slearn.membermanagement.service.MessageService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public final class TestMessageSupport {

    private TestMessageSupport() {
    }

    public static MessageService vietnamese() {
        return create(Locale.forLanguageTag("vi"));
    }

    public static MessageService english() {
        return create(Locale.ENGLISH);
    }

    public static MessageService create(Locale locale) {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        LocaleContextHolder.setLocale(locale);
        return new MessageService(source);
    }
}
