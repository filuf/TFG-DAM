package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.company.GetCompanyResponse
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.dtos.company.PatchCompanyResponse
import com.raj.slotify.dtos.company.SearchCompaniesResponse
import com.raj.slotify.dtos.user.UserSummary
import com.raj.slotify.models.api.PageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface CompanyService {

    @GET("companies/search")
    suspend fun getCompaniesByQuery(
        @Header("Authorization") authHeader: String,
        @Query("q") q: String,
        @Query("page") page: Int,
    ): Response<PageResponse<SearchCompaniesResponse>>

    @Multipart
    @PATCH("companies")
    suspend fun patchCompany(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part?,
        @Part("request") request: RequestBody
    ): Response<PatchCompanyResponse>

    @GET("companies/{companyId}")
    suspend fun getCompanyById(
        @Path("companyId") companyId: UUID,
        @Header("Authorization") authHeader: String,
        @Query("fetchMode") fetchMode: String?,
    ): Response<GetCompanyResponse>

    @GET("companies/{companyId}/services")
    suspend fun getServicesByCompanyId(
        @Path("companyId") companyId: UUID,
        @Header("Authorization") authHeader: String,
        @Query("fetchMode") fetchMode: String?,
        @Query("page") page: Int?,
        @Query("sortBy") sortBy: String?,
        @Query("order") order: String?
    ): Response<PageResponse<GetServicesResponse>>

}