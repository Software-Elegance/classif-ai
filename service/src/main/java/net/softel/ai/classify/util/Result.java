package net.softel.ai.classify.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Result<T>{
        
        private static final long serialVersionUID = 2405172041950251807L;

        private  boolean success = false;
        private  T data = null; 
        private  String msg = null;

        public Result(boolean success, T data) {
                this.success = success;
                this.data = data;
                this.msg = null;
                }

        public Result(boolean success, String msg) {
                this.success = success;
                this.data = null;
                this.msg = msg;
                }

        public Result(boolean success, T data, String msg){
                this.success = success;
                this.data = data;
                this.msg = msg;
                }

        }

