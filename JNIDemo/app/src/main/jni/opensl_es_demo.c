#include <pthread.h>

// 针对原生OpenSL ES音频
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

#include <SLES/OpenSLES_Platform.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>

static pthread_cond_t s_cond;
static pthread_mutex_t s_mutex;

static SLObjectItf engineObject = NULL;
static SLEngineItf engineEngine;
static SLObjectItf outputMixObject = NULL;
static SLObjectItf bqPlayerObject = NULL;
static SLPlayItf bqPlayerPlay;
static SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;

static void waitForPlayerCallback()
{
    pthread_mutex_lock(&s_mutex);
    pthread_cond_wait(&s_cond, &s_mutex);
    pthread_mutex_unlock(&s_mutex);
}

SLresult enqueueNextSample(short* sample, int size, short waitForCallback)
{
    if(waitForCallback)
    {
        waitForPlayerCallback();
    }
    return (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue,
                                           nextBuffer,
                                           nextSize);
}

void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context)
{
    pthread_cond_signal(&s_cond);
}

SLresult initOpenSLES()
{
    // 使用它检查每个操作的结果
    SLresult result;

    int speakers;
    int channels = 2;

    // 首先创建互斥量, 以便在稍后播放音频时使用
    pthread_cond_init(&s_cond, NULL);
    pthread_mutex_init(&s_mutex, NULL);

    // 创建和实现引擎
    result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    if(result != SL_RESULT_SUCCESS) return result;
    result = (*engineObject)->Realize(engineObject,
                                      SL_BOOLEAN_FALSE);
    if(result != SL_RESULT_SUCCESS) return result;
    result = (*engineObject)->GetInterface(engineObject,
                                           SL_IID_ENGINE,
                                           &engineEngine);
    if(result != SL_RESULT_SUCCESS) return result;

    // 创建和实现输出混音器
    const SLInterfaceID outputIds[1] = {SL_IID_VOLUME};
    const SLboolean outputReq[1] = {SL_BOOLEAN_FALSE};
    result = (*engineEngine)->CreateOutputMix(engineEngine,
                                              &outputMixObject,
                                              1,
                                              outputIds,
                                              outputReq);
    if(result != SL_RESULT_SUCCESS) return result;
    result = (*outputMixObject)->Realize(outputMixObject,
                                         SL_BOOLEAN_FALSE);
    if(result != SL_RESULT_SUCCESS) return result;

    // 设置输出缓冲器和接收器
    SLDataLocator_AndroidSimpleBufferQueue bufferQueue =
                       {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
    speakers = SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT;
    SLDataFormat_PCM formatPcm = {SL_DATAFORMAT_PCM,
                                  channels,
                                  SL_SAMPLINGRATE_44_1,
                                  SL_PCMSAMPLEFORMAT_FIXED_16,
                                  SL_PCMSAMPLEFORMAT_FIXED_16,
           speakers, SL_BYTEORDER_LITTLEENDIAN};
    SLDataSource audioSource = {&bufferQueue, &formatPcm};
    SLDataLocator_OutputMix dataLocOutputMix =
                                            {SL_DATALOCATOR_OUTPUTMIX,
                                            outputMixObject};
    SLDataSink audioSink = {&dataLocOutputMix, NULL};

    // 创建和实现播放器对象
    const SLInterfaceID playerIds[] =
                        {SL_IID_ANDROIDSIMPLEBUFFERQUEUE};
    const SLboolean playerReq[] = {SL_BOOLEAN_TRUE};
    result = (*engineEngine)->CreateAudioPlayer(engineEngine,
                                                &bqPlayerObject,
                                                &audioSource,
                                                &audioSink,
                                                1,
                                                playerIds,
                                                playerReq);
    if(result != SL_RESULT_SUCCESS) return result;
    result = (*bqPlayerObject)->Realize(bqPlayerObject,
                                        SL_BOOLEAN_FALSE);
    if(result != SL_RESULT_SUCCESS) return result;
    result = (*bqPlayerObject)->GetInterface(bqPlayerObject,
                                             SL_IID_PLAY,
                                             &bqPlayerPlay);
    if(result != SL_RESULT_SUCCESS) return result;

    // 获取播放器缓冲队列对象
    result = (*bqPlayerObject)->GetInterface(bqPlayerObject,
           SL_IID_ANDROIDSIMPLEBUFFERQUEUE, &bqPlayerBufferQueue);
    if(result != SL_RESULT_SUCCESS) return result;

    // 注册回调函数
    result = (*bqPlayerBufferQueue)->RegisterCallback(bqPlayerBufferQueue,
                                                      bqPlayerCallback,
                                                      NULL);
    if(result != SL_RESULT_SUCCESS) return result;

    return SL_RESULT_SUCCESS;
}

SLresult pausePlayback()
{
    return (*bqPlayerPlay)->SetPlayState(bqPlayerPlay,
                                         SL_PLAYSTATE_PAUSED);
}

SLresult startPlayback()
{
    return (*bqPlayerPlay)->SetPlayState(bqPlayerPlay,
                                         SL_PLAYSTATE_PLAYING);
}

void shutdownOpenSLES()
{
    if (bqPlayerObject != NULL) {
        (*bqPlayerObject)->Destroy(bqPlayerObject);
        bqPlayerObject = NULL;
        bqPlayerPlay = NULL;
        bqPlayerBufferQueue = NULL;
    }

    if (outputMixObject != NULL) {
        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject = NULL;
    }

    if (engineObject != NULL) {
        (*engineObject)->Destroy(engineObject);
        engineObject = NULL;
        engineEngine = NULL;
    }

    pthread_cond_destroy(&s_cond);
    pthread_mutex_destroy(&s_mutex);
}
