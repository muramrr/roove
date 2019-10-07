package com.mmdev.meetapp.ui.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.mmdev.meetapp.R





/* Created by A on 07.10.2019.*/

/**
 * This is the documentation block about the class
 */

class CustomAlertDialog {

	class Builder(private val context: Context) {

		private var title: String = ""
		private var message: String = ""
		private var positiveBtnText: String = ""
		private var negativeBtnText: String = ""
		private var posListener: View.OnClickListener? = null
		private var negListener: View.OnClickListener? = null
		private var pBtnColor: Int = 0
		private var nBtnColor: Int = 0
		private var bgColor: Int = 0
		private var cancel: Boolean = true

		fun setTitle(title: String): Builder {
			this.title = title
			return this
		}

		fun setBackgroundColor(bgColor: Int): Builder {
			this.bgColor = bgColor
			return this
		}

		fun setMessage(message: String): Builder {
			this.message = message
			return this
		}

		fun setPositiveBtnText(positiveBtnText: String): Builder {
			this.positiveBtnText = positiveBtnText
			return this
		}

		fun setPositiveBtnBackground(pBtnColor: Int): Builder {
			this.pBtnColor = pBtnColor
			return this
		}

		fun setNegativeBtnText(negativeBtnText: String): Builder {
			this.negativeBtnText = negativeBtnText
			return this
		}

		fun setNegativeBtnBackground(nBtnColor: Int): Builder {
			this.nBtnColor = nBtnColor
			return this
		}

		//set Positive listener
		fun OnPositiveClicked(posListener: View.OnClickListener): Builder {
			this.posListener = posListener
			return this
		}

		//set Negative listener
		fun OnNegativeClicked(nListener: View.OnClickListener): Builder {
			this.negListener = nListener
			return this
		}

		fun isCancellable(cancel: Boolean): Builder {
			this.cancel = cancel
			return this
		}

		fun build(): CustomAlertDialog {
			val dialog = Dialog(context)


			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
			dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			dialog.window?.attributes?.windowAnimations = R.style.AlertDialogAnimation
			dialog.setCancelable(cancel)

			dialog.setContentView(R.layout.dialog_alert)
			val message1: TextView = dialog.findViewById(R.id.message)
			val title1: TextView = dialog.findViewById(R.id.title)
			val negBtn: Button = dialog.findViewById(R.id.negativeBtn)
			val posBtn: Button = dialog.findViewById(R.id.positiveBtn)
			val view: View = dialog.findViewById(R.id.background)





			//getting resources

			title1.text = title
			if (title1.text.isEmpty()) title1.visibility = View.GONE
			message1.text = message
			if (message1.text.isEmpty()) message1.visibility = View.GONE

			if (positiveBtnText.isNotEmpty()) posBtn.text = positiveBtnText
			if (pBtnColor != 0) {
				val bgShape = posBtn.background as GradientDrawable
				bgShape.setColor(pBtnColor)
			}
			if (nBtnColor != 0) {
				val bgShape = negBtn.background as GradientDrawable
				bgShape.setColor(nBtnColor)
			}
			if (negativeBtnText.isNotEmpty()) negBtn.text = negativeBtnText


			if (bgColor != 0) view.setBackgroundColor(bgColor)
			if (posListener != null) {
				posBtn.setOnClickListener {
					posListener!!.onClick(posBtn)
					dialog.dismiss()
				}
			}
			else posBtn.setOnClickListener { dialog.dismiss() }


			if (negListener != null) {
				negBtn.setOnClickListener {
					negListener!!.onClick(negBtn)
					dialog.dismiss()
				}

			}
			else negBtn.setOnClickListener { dialog.dismiss() }


			dialog.show()

			return CustomAlertDialog()

		}


	}

}