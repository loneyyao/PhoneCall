package com.ajiew.phonecallapp.net;

import java.util.List;

public class AreaCallAddress {

    /**
     * success : 1
     * result : {"count":"1","lists":[{"areaid":"242","postcode":"528400","areacode":"0760","areanm":"中华人民共和国,广东省,中山市","simcall":"中国,广东,中山"}]}
     */

    private String success;
    private ResultBean result;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * count : 1
         * lists : [{"areaid":"242","postcode":"528400","areacode":"0760","areanm":"中华人民共和国,广东省,中山市","simcall":"中国,广东,中山"}]
         */

        private String count;
        private List<ListsBean> lists;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public List<ListsBean> getLists() {
            return lists;
        }

        public void setLists(List<ListsBean> lists) {
            this.lists = lists;
        }

        public static class ListsBean {
            /**
             * areaid : 242
             * postcode : 528400
             * areacode : 0760
             * areanm : 中华人民共和国,广东省,中山市
             * simcall : 中国,广东,中山
             */

            private String areaid;
            private String postcode;
            private String areacode;
            private String areanm;
            private String simcall;

            public String getAreaid() {
                return areaid;
            }

            public void setAreaid(String areaid) {
                this.areaid = areaid;
            }

            public String getPostcode() {
                return postcode;
            }

            public void setPostcode(String postcode) {
                this.postcode = postcode;
            }

            public String getAreacode() {
                return areacode;
            }

            public void setAreacode(String areacode) {
                this.areacode = areacode;
            }

            public String getAreanm() {
                return areanm;
            }

            public void setAreanm(String areanm) {
                this.areanm = areanm;
            }

            public String getSimcall() {
                return simcall;
            }

            public void setSimcall(String simcall) {
                this.simcall = simcall;
            }
        }
    }
}
