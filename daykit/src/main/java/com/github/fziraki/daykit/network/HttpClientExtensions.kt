package com.github.fziraki.daykit.network

import com.github.fziraki.daykit.result.DataError
import com.github.fziraki.daykit.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.SerializationException

suspend inline fun <reified Response : Any> HttpClient.getResult(
    route: String,
    queryParameters: Map<String, Any?> = emptyMap(),
): Result<Response, DataError.Network> =
    safeCall {
        get(route) {
            queryParameters.forEach { (key, value) ->
                if (value != null) {
                    url.parameters.append(key, value.toString())
                }
            }
        }
    }

suspend inline fun <reified Request, reified Response : Any> HttpClient.postResult(
    route: String,
    body: Request,
): Result<Response, DataError.Network> =
    safeCall {
        post(route) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

suspend inline fun <reified Response : Any> HttpClient.deleteResult(
    route: String,
    queryParameters: Map<String, Any?> = emptyMap(),
): Result<Response, DataError.Network> =
    safeCall {
        delete(route) {
            queryParameters.forEach { (key, value) ->
                if (value != null) {
                    url.parameters.append(key, value.toString())
                }
            }
        }
    }

suspend inline fun <reified T> safeCall(execute: suspend () -> HttpResponse): Result<T, DataError.Network> {
    val response =
        try {
            execute()
        } catch (e: SerializationException) {
            return Result.Error(DataError.Network.SERIALIZATION)
        } catch (e: TimeoutCancellationException) {
            return Result.Error(DataError.Network.REQUEST_TIMEOUT)
        } catch (e: IOException) {
            return Result.Error(DataError.Network.NO_INTERNET)
        } catch (e: ClientRequestException) {
            return mapHttpStatus(e.response.status.value)
        } catch (e: ServerResponseException) {
            return mapHttpStatus(e.response.status.value)
        } catch (e: RedirectResponseException) {
            return mapHttpStatus(e.response.status.value)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return Result.Error(DataError.Network.UNKNOWN)
        }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Network> =
    when (response.status.value) {
        in 200..299 ->
            try {
                Result.Success(response.body<T>())
            } catch (e: SerializationException) {
                Result.Error(DataError.Network.SERIALIZATION)
            }

        400 -> Result.Error(DataError.Network.BAD_REQUEST)
        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        403 -> Result.Error(DataError.Network.FORBIDDEN)
        404 -> Result.Error(DataError.Network.NOT_FOUND)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        503 -> Result.Error(DataError.Network.SERVICE_UNAVAILABLE)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }

fun <T> mapHttpStatus(statusCode: Int): Result<T, DataError.Network> =
    when (statusCode) {
        400 -> Result.Error(DataError.Network.BAD_REQUEST)
        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        403 -> Result.Error(DataError.Network.FORBIDDEN)
        404 -> Result.Error(DataError.Network.NOT_FOUND)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        503 -> Result.Error(DataError.Network.SERVICE_UNAVAILABLE)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }

