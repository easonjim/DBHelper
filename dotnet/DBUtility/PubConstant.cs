using System;
using System.Configuration;
using System.Web;

namespace Jsoft.DBUtility
{
    /// <summary>
    /// 公共工具类
    /// </summary>
    public class PubConstant
    {
        protected const string KEY_CONNECTION = "ConnectionString";
        protected const string KEY_ENCRYPT = "ConStringEncrypt";

        private const string SQLSERVERDAL = "Jsoft.SQLServerDAL";
        public static bool IsSQLServer = (GetConfigString("DAL") == SQLSERVERDAL);

        /// <summary>
        /// 获取固定连接字符串
        /// </summary>
        public static string ConnectionString
        {
            get
            {
                //更新缓存
                ConfigurationManager.RefreshSection("appSettings");
                string connectionString = ConfigurationManager.AppSettings[KEY_CONNECTION];
                string conStringEncrypt = ConfigurationManager.AppSettings[KEY_ENCRYPT];
                if (conStringEncrypt == "true")
                {
                    connectionString = DESEncrypt.Decrypt(connectionString);
                }
                return connectionString;
            }
        }

        /// <summary>
        /// 动态得到web.config里配置项的数据库连接字符串。
        /// </summary>
        /// <param name="configName"></param>
        /// <returns></returns>
        public static string GetConnectionString(string configName)
        {
            //更新缓存
            ConfigurationManager.RefreshSection("appSettings");
            string connectionString = ConfigurationManager.AppSettings[configName];
            string conStringEncrypt = ConfigurationManager.AppSettings[KEY_ENCRYPT];
            if (conStringEncrypt == "true")
            {
                connectionString = DESEncrypt.Decrypt(connectionString);
            }
            return connectionString;
        }

        /// <summary>
        /// 从缓存中得到AppSettings中的配置字符串信息
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        public static string GetConfigString(string key)
        {
            string CacheKey = "AppSettings-" + key;
            object objModel = GetCache(CacheKey);
            if (objModel == null)
            {
                try
                {
                    objModel = ConfigurationManager.AppSettings[key];
                    if (objModel != null)
                    {
                        int CacheTime = 30;
                        SetCache(CacheKey, objModel, DateTime.Now.AddMinutes(CacheTime), TimeSpan.Zero);
                    }
                }
                catch
                { }
            }
            return objModel.ToString();
        }

        /// <summary>
        /// 获取当前应用程序指定CacheKey的Cache值
        /// </summary>
        /// <param name="CacheKey"></param>
        /// <returns></returns>
        public static object GetCache(string CacheKey)
        {
            System.Web.Caching.Cache objCache = HttpRuntime.Cache;
            return objCache[CacheKey];

        }
        /// <summary>
        /// 设置当前应用程序指定CacheKey的Cache值
        /// </summary>
        /// <param name="CacheKey"></param>
        /// <param name="objObject"></param>
        public static void SetCache(string CacheKey, object objObject, DateTime absoluteExpiration, TimeSpan slidingExpiration)
        {
            System.Web.Caching.Cache objCache = HttpRuntime.Cache;
            objCache.Insert(CacheKey, objObject, null, absoluteExpiration, slidingExpiration);
        }
    }
}
