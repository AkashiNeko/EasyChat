package src.window;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @BelongsProject: EasyChat
 * @FileName: TextFieldSetting
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 文本输入框格式限定
 */
public class TextFieldSetting {
    public static void setMaxCharLimit(JTextField textField, final int limit) {
        Document document = new PlainDocument() {
            @Override
            public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
                if (str == null)
                    return;
                if ((getLength() + str.length()) <= limit)
                    super.insertString(offset, str, attr);
            }
        };
        textField.setDocument(document);
    }

    // 限制只能输入字母、数字或下划线
    public static void setLimitedChars(JTextField textField) {
        Document document = textField.getDocument();
        if (document instanceof PlainDocument) {
            PlainDocument plainDocument = (PlainDocument) document;
            plainDocument.setDocumentFilter(new LimitedCharsFilter());
        }
    }

    public static class LimitedCharsFilter extends javax.swing.text.DocumentFilter {
        private static final String REGEX_PATTERN = "^[a-zA-Z0-9_]*$";

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            if (text == null) {
                return;
            }

            String newValue = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
            if (newValue.matches(REGEX_PATTERN)) {
                super.insertString(fb, offset, text, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) {
                return;
            }

            String newValue = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
            if (newValue.matches(REGEX_PATTERN)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}
