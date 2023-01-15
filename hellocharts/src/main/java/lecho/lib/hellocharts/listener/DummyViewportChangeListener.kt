package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.Viewport

class DummyViewportChangeListener : ViewportChangeListener {
    override fun onViewportChanged(newViewport: Viewport) {
        // Do nothing
    }
}