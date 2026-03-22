package com.damon.aicode.core.saver;

import cn.hutool.core.text.CharSequenceUtil;
import com.damon.aicode.ai.enums.CodeGenTypeEnum;
import com.damon.aicode.ai.model.HtmlCodeResult;
import com.damon.aicode.exception.BusinessException;
import com.damon.aicode.exception.ErrorCode;

/**
 * HTML代码文件保存器
 *
 * @author yangjilin
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        // 保存 HTML 文件
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        // HTML 代码不能为空
        if (CharSequenceUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
        }
    }
}
