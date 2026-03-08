/*
  Copyright 2022 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/
package com.adobe.marketing.mobile.messagingsample

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PayloadFormatUtils private constructor() {
    companion object {
        @Throws(JSONException::class)
        fun toMap(jsonObject: JSONObject?): Map<String, Any?>? {
            return jsonObject?.let {
                val jsonAsMap: MutableMap<String, Any?> = HashMap()
                val keysIterator = jsonObject.keys() ?: return null
                while (keysIterator.hasNext()) {
                    val nextKey = keysIterator.next()
                    jsonAsMap[nextKey] = fromJson(jsonObject[nextKey])
                }
                return jsonAsMap
            }
        }

        @Throws(JSONException::class)
        fun toObjectMap(jsonObject: JSONObject?): Map<String, Any?>? {
            return jsonObject?.let {
                val jsonAsObjectMap: MutableMap<String, Any?> = HashMap()
                val keysIterator = jsonObject.keys()
                while (keysIterator.hasNext()) {
                    val nextKey = keysIterator.next()
                    val value = fromJson(jsonObject[nextKey])
                    jsonAsObjectMap[nextKey] = value
                }
                return jsonAsObjectMap
            }
        }

        @Throws(JSONException::class)
        fun toList(jsonArray: JSONArray?): List<Any?>? {
            return jsonArray?.let {
                val jsonArrayAsList: MutableList<Any?> = ArrayList()
                val size = jsonArray.length()
                for (i in 0 until size) {
                    jsonArrayAsList.add(fromJson(jsonArray[i]))
                }
                return jsonArrayAsList
            }
        }

        @Throws(JSONException::class)
        private fun fromJson(json: Any): Any? {
            return if (json === JSONObject.NULL) {
                null
            } else if (json is JSONObject) {
                toMap(json)
            } else if (json is JSONArray) {
                toList(json)
            } else {
                json
            }
        }
    }
}
