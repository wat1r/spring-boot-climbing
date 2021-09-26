## 修改HttpServletRequest的URI





```java
@Override
public StringBuffer getRequestURL() {
    StringBuffer originalUrl = ((HttpServletRequest) getRequest()).getRequestURL();
    log.info("originalUrl:{}", originalUrl);
    return new StringBuffer(originalUrl + "?category=1");
}
```







```java
@Component
@Slf4j
public class HttpServletRequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        try {
            MyHttpServletRequestWrapper wrapper = null;
            String freshUrl = null;
            if (servletRequest instanceof HttpServletRequest) {
                log.info("inside filter");
                wrapper = new MyHttpServletRequestWrapper((HttpServletRequest) servletRequest);
                String originalUrl = ((HttpServletRequest) servletRequest).getRequestURI();
                log.info("originalUrl:{}", originalUrl);
                if (originalUrl.contains("local")) {
                    originalUrl = originalUrl.replace("/studio", "");
                    freshUrl = originalUrl + "?category=1";
                }
            }
            if (wrapper == null) {
                filterChain.doFilter(servletRequest, servletResponse);
                log.info("wapper is  null");
            } else {
                if (freshUrl == null) {
                    filterChain.doFilter(wrapper, servletResponse);
                } else {
                    wrapper.getRequestDispatcher(freshUrl).forward(wrapper, servletResponse);
                }

            }
        } catch (Exception ex) {
            log.error("error inside filter:{}", ex.getMessage());
        }
    }

    
    
    /**
     * @param uri
     * @return
     */
    private static UriVO filterURI(String uri) {
        UriVO res = null;
        List<Pattern> patList = getPatternList();
        for (Pattern pattern : patList) {
            Matcher matcher = pattern.matcher(uri);
            if (matcher.matches()) {
                switch (matcher.group(1)) {
                    case URI_PREFIX_REALTIME:
                        res = splitURI(uri, URI_PREFIX_REALTIME);
                        return res;
                    case URI_PREFIX_OFFLINE:
                        res = splitURI(uri, URI_PREFIX_OFFLINE);
                        return res;
                    default:
                        break;
                }
            }
        }
        log.info("the new uri:{}", uri);
        return res;
    }


    /**
     * 切分
     *
     * @param uri
     * @param categoryStr
     * @return
     */
    private static UriVO splitURI(String uri, String categoryStr) {
        UriVO res = null;
        Matcher mat = Pattern.compile("(?<=" + categoryStr + ")(.*)", Pattern.MULTILINE).matcher(uri);
        if (mat.find()) {
            uri = mat.group(0) + "?category=" + (categoryStr.equals(URI_PREFIX_REALTIME) ? TASK_TYPE_DICT_REALTIME : TASK_TYPE_DICT_OFFLINE);
            res = new UriVO(true, uri);
        }
        return res;
    }


    private static List<Pattern> getPatternList() {
        return new ArrayList<Pattern>() {{
            add(Pattern.compile("/studio/(\\w+)/file/local/(\\w{8}(-\\w{4}){3}-\\w{12}?)")); //{fileId}
            add(Pattern.compile("/studio/(\\w+)/dir/list/all"));
            add(Pattern.compile("/studio/(\\w+)/dir/list"));
            add(Pattern.compile("/studio/(\\w+)/dir/list/setting"));
            add(Pattern.compile("/studio/(\\w+)/dir/search"));
            add(Pattern.compile("/studio/(\\w+)/recycleBin/list"));
            add(Pattern.compile("/studio/(\\w+)/dir/create"));
            add(Pattern.compile("/studio/(\\w+)/dir/move"));
            add(Pattern.compile("/studio/(\\w+)/file/local/task/list/all"));
            add(Pattern.compile("/studio/(\\w+)/file/local/task/list/me"));
        }};
    }


    @Data
    @AllArgsConstructor
    static class UriVO {
        private boolean isFind;
        private String uri;
    }

}
```











### Reference

- [how to use a servlet filter in Java to change an incoming servlet request url?](https://stackoverflow.com/questions/2725102/how-to-use-a-servlet-filter-in-java-to-change-an-incoming-servlet-request-url)

- [使用HttpServletRequestWrapper重写Request请求参数](https://www.cnblogs.com/niunafei/p/13791814.html)