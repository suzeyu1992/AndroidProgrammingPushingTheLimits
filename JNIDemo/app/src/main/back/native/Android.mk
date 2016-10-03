# 用于返回当前目录的路径
LOCAL_PATH := $(call my-dir)

# CLEAR_VARS 变量是由生成系统已提供的，
# 并且指出一个特殊的 GNU Makefile 文件将为你清除除了 LOCAL_PATH 以外的许多的 LOCAL_XXX 变量，
# (例如：LOCAL_MODULE, LOCAL_SRC_FILES, LOCAL_STATIC_LIBRARIES,等等...)
# 这是必须的，因为全部的生成控制文件是在一个单独的 GNU Make 执行环境中被分析的，在那里所有的变量是全局的。
include $(CLEAR_VARS)

# LOCAL_MODULE 变量必须是已定义的，用来标识你的 Android.mk 文件中描述的每个模块。
# 模块名字必须是唯一，并且不能包含任何的空格。
# 注意生成系统将自动添加适当的前缀和后缀到相应的产生文件。
# 换句话说，一个共享库模块命名为 native-activity 将产生 libnative-activity.so 。
LOCAL_MODULE    := native-activity

# LOCAL_SRC_FILES 变量必须包含将生成且汇编成一个模块的 C 和/或 C++ 源文件的列表。
# 注意你将不列出头文件和包含文件在这里，因为生成系统将自动地为你估算依赖；
# 列出的源文件将直接递给编译器。
LOCAL_SRC_FILES := main.c

# 使用在生成你的模块时的额外的链接器标志列表。
# 对于用 -l 前缀传递特定的系统库名是有用的。
# liblog.so       提供 Android 记录日志 API
# libandroid.so   提供 Android 功能访问 API
# libEGL.so       提供 EGL API
# libGLESv1_CM.so 提供 OpenGL ES API
# 注：
# OpenGL ES (OpenGL for Embedded Systems，以下简称 OpenGL)
# OpenGL 三维图形 API 的子集，针对手机、PDA和游戏主机等嵌入式设备而设计。
# 该 API 由 Khronos 集团定义推广，
# Khronos 是一个图形软硬件行业协会，该协会主要关注图形和多媒体方面的开放标准。
#
# EGL 是 OpenGL ES 和 底层本地平台视窗系统 之间的接口，为 OpenGL ES 提供平台独立性而设计。
# 它被用于处理图形管理、表面/缓冲捆绑、渲染同步，
# 以及支援使用其他 Khronos API 进行的高效、加速、混合模式 2D 和 3D 渲染。
LOCAL_LDLIBS    := -llog -landroid -lEGL -lGLESv1_CM

# 将链接到本模块的静态库模块列表(用 BUILD_STATIC_LIBRARY 生成的)。
# 这仅在共享库模块中有意义。
LOCAL_STATIC_LIBRARIES := android_native_app_glue

# BUILD_SHARED_LIBRARY 是一个已由生成系统提供的变量，
# 表明一个 GNU Makefile 脚本是负责收集你定义的从最近的 include $(CLEAR_VARS)
# 到决定去生成之间的全部 LOCAL_XXX 变量的信息，然后正确地生成共享库。
# 注意你必须在包含这个文件之前最近位置有 LOCAL_MODULE 或 LOCAL_SRC_FILES 变量的定义。
include $(BUILD_SHARED_LIBRARY)

# $(call macro-name[, param1, ...])
# call 是一个内置于 make 的函数，
# call 会扩展它的第一个参数并把其余参数依次替换到出现 $1、$2、...的地方。
# call 的第一个参数可以是任何宏或变量的名称。
# 允许你通过名字查找且包含其它模块的 Android.mk 文件。
# 这将在你的 NDK_MODULE_PATH 环境变量提到的目录列表中查找模块标记的名字，
# 并自动地为你包含它的 Android.mk 文件。
# 为了方便起见，$NDK/sources 是被 NDK 生成系统附加到你的 NDK_MODULE_PATH 变量值定义中。
$(call import-module,android/native_app_glue)
