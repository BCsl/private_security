package com.tools.security.utils.volley.uploadimg;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 上传图片Request
 * Created by xdf on 2015/9/23.
 */
public class PostUploadRequest extends Request<String> {

    /**
     * 正确数据的时候回掉用
     */
    private ResponseListener mListener;
    /*请求 参数的传入*/
    private Map<String, String> strParams;
    private Map<String, File> fileParams;

    // default charset
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String CRLF = "\r\n";

    private static final String HYPHENS = "--";

    private static final String BOUNDARY = "***BATMOBI***";

    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public PostUploadRequest(String url, Map<String, String> strParams, Map<String, File> fileParams, ResponseListener listener) {
        super(Method.POST, url, listener);
        this.mListener = listener;
        this.strParams = strParams;
        this.fileParams = fileParams;
        //设置请求的响应事件，因为文件上传需要较长的时间，所以在这里加大了，设为10秒
        setRetryPolicy(new DefaultRetryPolicy(10 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * 这里开始解析数据
     *
     * @param response Response from the network
     * @return
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String mString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            return Response.success(mString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 回调正确的数据
     *
     * @param response The parsed response returned by
     */
    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // 组合参数
            final String data;
            data = mapToMultiRequestData(strParams);
            byte[] fileData = mapToMultiRequestFileData(fileParams);
            // 往输出流中写数据
            // 正文开始
            bos.write(CRLF.getBytes(DEFAULT_CHARSET));
            // 文字内容
            bos.write(data.getBytes(DEFAULT_CHARSET));
            // 文件内容
            bos.write(fileData);
            // 正文结束
            bos.write((HYPHENS + BOUNDARY + HYPHENS).getBytes(DEFAULT_CHARSET));
            bos.flush();
            bos.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    //Content-Type: multipart/form-data; boundary=----------8888888888888
    @Override
    public String getBodyContentType() {
        return MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY;
    }

    /**
     * 组合参数
     */
    private static String mapToMultiRequestData(Map<String, String> params) throws UnsupportedEncodingException {
        if (params == null || params.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (TextUtils.isEmpty(entry.getKey()) || entry.getValue() == null) continue;
            sb.append(HYPHENS + BOUNDARY + CRLF);
            sb.append("Content-Disposition: form-data; name=\""
                    + entry.getKey() + "\"" + CRLF);
            sb.append("Content-Type: text/plain; charset=" + DEFAULT_CHARSET + CRLF);
            sb.append(CRLF);
            sb.append(entry.getValue());
            sb.append(CRLF);
        }
        return sb.toString();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    /**
     * 读取所有文件的数据转换成Byte[]
     */
    private static byte[] mapToMultiRequestFileData(Map<String, File> params) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            for (Map.Entry<String, File> entry : params.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                dos.writeBytes(HYPHENS + BOUNDARY + CRLF);
                dos.writeBytes("Content-Disposition: form-data; name=\""
                        + entry.getKey() + "\" ;filename=\"" + entry.getValue().getName()
                        + CRLF);
                dos.writeBytes(CRLF);
                FileInputStream is = new FileInputStream(entry.getValue());
                byte[] buffer = new byte[1024];
                int length = -1;
                while ((length = is.read(buffer)) != -1) {
                    dos.write(buffer, 0, length);
                }
                is.close();
                dos.writeBytes(CRLF);
                dos.flush();
            }
            return bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
            }
        }
    }
}